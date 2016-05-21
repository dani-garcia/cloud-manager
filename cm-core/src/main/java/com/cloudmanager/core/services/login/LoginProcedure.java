package com.cloudmanager.core.services.login;

import com.cloudmanager.core.model.FileRepo;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Represents a service login procedure.
 * <p>
 * The procedure consists of three separate steps:
 * <p>
 * - preLogin is used to set any necessary info before user input, and to prepare the login form.
 * <p>
 * - The second step is the user action, it can consist of the user filling a form in the application or opening a web page for OAuth
 * <p>
 * - The last step, postLogin can be done in two ways:
 * <p>
 * --- Manual postLogin, used when filling a form on the application and the user clicks submit.
 * <p>
 * --- Automatic postLogin, used with OAuth, and the user authorizes the application on a web browser. Using this method,
 * the browser would send the authorization code to a local web server started by the application without any user action.
 */
public interface LoginProcedure {

    /**
     * Prepares the login procedure and the login form. Use this step to generate a URL in the case of OAuth
     * and to include the necessary fields in the case of a normal login
     *
     * @param repoName The repo name the user has chosen
     */
    void preLogin(String repoName);

    /**
     * Returns the list of fields to show on the login window.
     *
     * @return The field list
     */
    List<LoginField> getFields();

    /**
     * Used to check if the postLogin step is done manually. In that case, the form should
     * include a submit button to call the {@link #postLogin()} method.
     *
     * @return True if the postLogin step is manual.
     */
    boolean isPostLoginManual();

    /**
     * Completes the login. Only use if {@link #isPostLoginManual} is true.
     *
     * @return True if the procedure completed successfully
     */
    boolean postLogin();

    /**
     * Adds a completion listener. Necessary for the automatic postLogin and recommended for the manual postLogin.
     * <p>
     * The listener is called if the procedure completes. The first parameter is the completion status:
     * true for success and false if there was any error. The second parameter contains the repository
     * created, and should be added to the list.
     *
     * @param listener The listener to add
     */
    void addLoginCompleteListener(BiConsumer<Boolean, FileRepo> listener);

    /**
     * Cancels the login procedure. In the case of OAuth login, the web server should be stopped here.
     */
    void cancel();

}
