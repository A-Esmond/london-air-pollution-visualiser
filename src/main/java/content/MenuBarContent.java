package content;

import core.DataHandler;
import core.Display;
import menus.TopMenuBarInfo;
import panels.MapPanel;
import panels.Panel;
import panels.StatisticsPanel;
import panels.WelcomePanel;

public class MenuBarContent {

    public static TopMenuBarInfo navigationInfo(Display display, DataHandler dataHandler) {
        TopMenuBarInfo info = new TopMenuBarInfo();

        Panel welcomePanel = new WelcomePanel("Welcome", display, dataHandler);
        Panel statisticsPanel = new StatisticsPanel("Stats", display, dataHandler);
        Panel mapPanel = new MapPanel("Map", display, dataHandler);

        info.addPanels(welcomePanel, statisticsPanel, mapPanel);

        info.setDefaultPanel(welcomePanel.getName());

        return info;
    }
}