package com.automation.framework.presentation.pages.dsm.pigmentation;

import com.automation.framework.infrastructure.api.BrowserSession;
import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.presentation.actions.dsm.pigmentation.PigmentationAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.time.Duration;

public class PigmentationPage {

    private static final String PIGMENTATION_URL =
            "https://pigmentation.dev.calculator.dsm-firmenich.com/";
    private static final String PIGMENTATION_HOST = "pigmentation.dev.calculator.dsm-firmenich.com";
    // Depois do login a Microsoft devolve para /auth/callback, que redireciona para a home
    private static final String AUTH_CALLBACK_PATH = "/auth/callback";
    private static final String MICROSOFT_LOGIN_HOST = "login.microsoftonline.com";
    private static final String HOME_TITLE = "Pigmentation calculator";
    // Provisório: sessão (cookies + storage) salva depois do login para pular o login da Microsoft
    // nas próximas execuções. Fica fora do git (.auth/ no .gitignore) porque contém os tokens.
    private static final Path SESSION_FILE =
            Path.of(System.getProperty("user.dir"), ".auth", "pigmentation-session.json");
    // Arquivo estático do próprio host: abre a origem sem disparar o redirect para a Microsoft,
    // que é onde dá para gravar o localStorage/sessionStorage antes de abrir o site
    private static final String SAME_ORIGIN_STATIC_URL = PIGMENTATION_URL + "favicon.ico";

    // O site redireciona direto para o login corporativo da Microsoft (sem tela intermediária)
    private final By microsoftEmailInput = By.name("loginfmt");
    private final By microsoftPasswordInput = By.name("passwd");
    private final By microsoftSubmitButton = By.id("idSIButton9");
    // Tela "Continuar conectado?": "Sim" grava o cookie persistente da Microsoft,
    // que vai junto na sessão salva (o "Sim" usa o mesmo id do botão de submit)
    private final By microsoftStaySignedInText = By.id("KmsiDescription");
    // MFA: número que precisa ser aprovado no Microsoft Authenticator
    private final By microsoftMfaNumber = By.id("idRichContext_DisplaySign");
    private static final int MFA_APPROVAL_TIMEOUT_SECONDS = 180;

    // Home: botão principal "Start Calculator →"
    private final By startCalculatorButton =
            By.xpath("//button[contains(normalize-space(.), 'Start Calculator')]");

    // Calculadora (Pigmentation Analysis), passo 1: Characteristics
    private static final String SIMULATION_PATH = "/simulation";
    private final By simulationDetailsTitle =
            By.xpath("//h1[normalize-space(.)='Simulation Details']");
    private final By customerNameInput = By.id("customerName");

    private final PigmentationAction actions;
    private final WebDriver driver;

    public PigmentationPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new PigmentationAction(driver, 20);
    }

    public void navigateToPigmentation() {
        driver.get(PIGMENTATION_URL);
        actions.sleep(3000);
    }

    // --- Helpers usados pelos testes de segurança (PenTest) ----------------------------------

    // URL base do app, para os testes montarem rotas internas (ex.: baseUrl() + "simulation")
    public String baseUrl() {
        return PIGMENTATION_URL;
    }

    public String host() {
        return PIGMENTATION_HOST;
    }

    // Arquivo estático do próprio host: abre a origem sem disparar o redirect para a Microsoft,
    // ponto em que dá para ler/limpar o localStorage/sessionStorage da origem do app
    public String sameOriginStaticUrl() {
        return SAME_ORIGIN_STATIC_URL;
    }

    public void refreshPage() {
        driver.navigate().refresh();
        actions.sleep(3000);
    }

    public boolean isOnMicrosoftLogin() {
        return driver.getCurrentUrl().contains(MICROSOFT_LOGIN_HOST);
    }

    // Conteúdo autenticado do Pigmentation: estamos na origem do app (fora do callback e do login
    // da Microsoft) e alguma tela logada está renderizada (home ou a calculadora).
    // Checagem rápida (sem espera), usada pelos testes de segurança.
    public boolean isAuthenticated() {
        String url = driver.getCurrentUrl();
        if (url.contains(MICROSOFT_LOGIN_HOST) || !url.contains(PIGMENTATION_HOST)
                || url.contains(AUTH_CALLBACK_PATH)) {
            return false;
        }
        return !driver.findElements(startCalculatorButton).isEmpty()
                || !driver.findElements(simulationDetailsTitle).isEmpty()
                || !driver.findElements(customerNameInput).isEmpty();
    }

    public void validateMicrosoftLoginPage() {
        actions.waitForVisible(microsoftEmailInput, Duration.ofSeconds(30));
        if (!driver.getCurrentUrl().contains(MICROSOFT_LOGIN_HOST)) {
            throw new AssertionError("Microsoft login page was not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    public void doPigmentationLogin() {
        LoginCredentials credentials = CredentialsProvider.pigmentation();
        doPigmentationLogin(credentials.username(), credentials.password());
    }

    public void doPigmentationLogin(String username, String password) {
        // Se o SSO corporativo ainda está válido, a Microsoft devolve direto para o app sem mostrar
        // o formulário de e-mail. Nesse caso não há login a digitar — o app já voltou autenticado.
        if (!waitForMicrosoftEmailFormOrApp()) {
            waitForMicrosoftLoginToFinish();
            return;
        }
        actions.sendKeys(microsoftEmailInput, username);
        actions.click(microsoftSubmitButton);

        actions.waitForVisible(microsoftPasswordInput, Duration.ofSeconds(30));
        // A tela de senha tem uma animação de entrada; digitar antes dela terminar perde o texto
        actions.sleep(1000);
        actions.sendKeys(microsoftPasswordInput, password);
        actions.click(microsoftSubmitButton);
        actions.sleep(3000);
        waitForMicrosoftLoginToFinish();
    }

    // Espera o formulário de e-mail da Microsoft aparecer.
    // Retorna false se, em vez disso, o SSO já devolveu para o app (sessão corporativa ainda válida);
    // retorna true quando o campo de e-mail está pronto para o login manual.
    // Se nenhum dos dois acontecer no tempo, delega a validateMicrosoftLoginPage para o erro informativo.
    private boolean waitForMicrosoftEmailFormOrApp() {
        for (int second = 0; second < 30; second++) {
            String url = driver.getCurrentUrl();
            if (url.contains(PIGMENTATION_HOST) && !url.contains(AUTH_CALLBACK_PATH)) {
                return false;
            }
            if (url.contains(MICROSOFT_LOGIN_HOST) && actions.isDisplayed(microsoftEmailInput)) {
                return true;
            }
            actions.sleep(1000);
        }
        validateMicrosoftLoginPage();
        return true;
    }

    // Fica na Microsoft até sair para o site: espera a aprovação do MFA no Authenticator
    // (manual, no celular) e responde "Sim" no "Continuar conectado?"
    private void waitForMicrosoftLoginToFinish() {
        boolean mfaLogged = false;
        for (int second = 0; second < MFA_APPROVAL_TIMEOUT_SECONDS; second++) {
            if (!driver.getCurrentUrl().contains(MICROSOFT_LOGIN_HOST)) {
                return;
            }
            if (actions.isDisplayed(microsoftStaySignedInText)) {
                actions.click(microsoftSubmitButton);
                actions.sleep(2000);
                continue;
            }
            if (!mfaLogged && actions.isDisplayed(microsoftMfaNumber)) {
                System.out.println("[Pigmentation] Approve the sign-in in Microsoft Authenticator. Number: "
                        + actions.getText(microsoftMfaNumber));
                mfaLogged = true;
            }
            actions.sleep(1000);
        }
        throw new AssertionError("Microsoft login did not finish in " + MFA_APPROVAL_TIMEOUT_SECONDS
                + "s (MFA not approved?). Current URL: " + driver.getCurrentUrl());
    }

    public void startCalculator() {
        actions.click(startCalculatorButton);
    }

    // Passo 1 (Characteristics) da nova simulação, em /simulation
    public void validateCalculatorPage() {
        actions.waitForVisible(simulationDetailsTitle, Duration.ofSeconds(30));
        actions.waitForVisible(customerNameInput, Duration.ofSeconds(10));
        if (!driver.getCurrentUrl().contains(SIMULATION_PATH)) {
            throw new AssertionError("Pigmentation calculator was not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    // Login reaproveitando a sessão salva; se ela não existir ou tiver expirado,
    // faz o login completo na Microsoft e salva a sessão nova para as próximas execuções
    public void doPigmentationLoginReusingSession() {
        if (loginWithSavedSession()) {
            return;
        }
        BrowserSession.deleteSession(SESSION_FILE);
        navigateToPigmentation();
        doPigmentationLogin();
        validateHomePage();
        saveLoginSession();
    }

    // Retorna true se a sessão salva levou direto para a home
    public boolean loginWithSavedSession() {
        driver.get(SAME_ORIGIN_STATIC_URL);
        if (!BrowserSession.restoreSession(driver, SESSION_FILE)) {
            System.out.println("[Pigmentation] No saved session at " + SESSION_FILE);
            return false;
        }
        navigateToPigmentation();
        for (int attempt = 0; attempt < 30; attempt++) {
            String url = driver.getCurrentUrl();
            if (url.contains(MICROSOFT_LOGIN_HOST) && actions.isDisplayed(microsoftEmailInput)) {
                System.out.println("[Pigmentation] Saved session expired, doing a full login");
                return false;
            }
            if (url.contains(PIGMENTATION_HOST) && !url.contains(AUTH_CALLBACK_PATH)
                    && actions.isDisplayed(startCalculatorButton)) {
                System.out.println("[Pigmentation] Logged in with the saved session");
                return true;
            }
            actions.sleep(1000);
        }
        return false;
    }

    // Precisa ser chamado com o site aberto (home), para pegar o storage da origem certa
    public void saveLoginSession() {
        BrowserSession.saveSession(driver, SESSION_FILE);
        System.out.println("[Pigmentation] Session saved to " + SESSION_FILE);
    }

    public void validateHomePage() {
        for (int attempt = 0; attempt < 40; attempt++) {
            String url = driver.getCurrentUrl();
            if (url.contains(PIGMENTATION_HOST) && !url.contains(AUTH_CALLBACK_PATH)) {
                break;
            }
            actions.sleep(1000);
        }
        actions.waitForVisible(startCalculatorButton, Duration.ofSeconds(30));
        if (!driver.getTitle().equals(HOME_TITLE)) {
            throw new AssertionError("Pigmentation home page was not displayed. Title: '"
                    + driver.getTitle() + "', URL: " + driver.getCurrentUrl());
        }
    }
}
