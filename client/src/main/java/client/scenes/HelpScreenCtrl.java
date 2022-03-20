package client.scenes;

import javax.inject.Inject;

public class HelpScreenCtrl {

    private final MainCtrl mainCtrl;

    /**
     * Constructor for help screen controller, which controls the interaction of the help screen
     * @param mainCtrl The main control which is used for calling methods to switch scenes
     */
    @Inject
    public HelpScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Return to the main overview
     */
    public void showMain() {
        mainCtrl.showMainScreen();
    }


}
