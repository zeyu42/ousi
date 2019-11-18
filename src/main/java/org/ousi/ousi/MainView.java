package org.ousi.ousi;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;



/**
 * The main view contains a button and a click listener.
 */
@Route("")
@PWA(name = "Ousi", shortName = "Ousi")
public class MainView extends AppLayout {

    private Ousi ousi = new Ousi();
    private Grid<Network> networkGrid = new Grid<>();

    public MainView() {
        // Navigation Bar
        // Logo (Currently using Vaadin's logo)
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        img.setHeight("44px");
        // Menu Bar
        MenuBar menu = new MenuBar();
        // File...
        MenuItem fileMenuItem = menu.addItem("File");
        SubMenu fileSubMenu = fileMenuItem.getSubMenu();
        fileSubMenu.addItem("Open");
        fileSubMenu.addItem("Save");
        fileSubMenu.addItem("Settings");

        // Generate...
        MenuItem generateMenuItem = menu.addItem("Generate");
        SubMenu generateSubMenu = generateMenuItem.getSubMenu();
        generateSubMenu.addItem("Random Network", event -> showCreateRandomNetworkDialog());

        // Transform...
        MenuItem transformMenuItem = menu.addItem("Transform");
        SubMenu transformSubMenu = transformMenuItem.getSubMenu();
        transformSubMenu.addItem("Add");

        // Analyze...
        MenuItem analyzeMenuItem = menu.addItem("Analyze");
        SubMenu analyzeSubMenu = analyzeMenuItem.getSubMenu();
        analyzeSubMenu.addItem("Density");

        // Help...
        MenuItem helpMenuItem = menu.addItem("Help");
        SubMenu helpSubMenu = helpMenuItem.getSubMenu();
        helpSubMenu.addItem("Help");
        helpSubMenu.addItem("About", event -> showAboutDialog());

        addToNavbar(true, img, menu);

        // Main Layout
        SplitLayout mainLayout = new SplitLayout();

        SplitLayout leftLayout = new SplitLayout();
        leftLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        SplitLayout visualizationLayout = new SplitLayout(); // Use this only when user displays >1 graphs
        Accordion outputAccordion = new Accordion();
        ousi.setOutputAccordion(outputAccordion);
        leftLayout.addToPrimary(visualizationLayout);
        leftLayout.addToSecondary(outputAccordion);
        leftLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);


        networkGrid.setItems(ousi.getNetworks());
        networkGrid.addColumn(Network::getLabel).setHeader("Label");
        networkGrid.addColumn(Network::getDescription).setHeader("Description");

        mainLayout.addToPrimary(leftLayout);
        mainLayout.addToSecondary(networkGrid);
        mainLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

        mainLayout.setSizeFull();
        leftLayout.setSizeFull();
        networkGrid.setSizeFull();
        leftLayout.setSplitterPosition(62);
        mainLayout.setSplitterPosition(62);

        setContent(mainLayout);
    }

    private void showCreateRandomNetworkDialog() {
        // If user's input is invalid, an exception will be thrown and the program will on running.
        Dialog createRandomNetworkDialog = new Dialog();
        FormLayout layout = new FormLayout();
        TextField nTextField = new TextField();
        nTextField.setAutofocus(true);
        nTextField.setLabel("# of Nodes");
        nTextField.setPlaceholder("10");
        TextField pTextField = new TextField();
        pTextField.setLabel("Link prob.");
        pTextField.setPlaceholder("0.5");
        layout.add(nTextField, pTextField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button createButton = new Button("Create", event -> {
            if (nTextField.getValue().equals("")) {
                nTextField.setValue(nTextField.getPlaceholder());
            }
            if (pTextField.getValue().equals("")) {
                pTextField.setValue(pTextField.getPlaceholder());
            }
            int n = Integer.parseInt(nTextField.getValue());
            double p = Double.parseDouble(pTextField.getValue());
            ousi.createRandomNetwork(n, p);
            networkGrid.getDataProvider().refreshAll();
            createRandomNetworkDialog.close();
        });
        createButton.addClickShortcut(Key.ENTER);
        Button cancelButton = new Button("Cancel", event -> createRandomNetworkDialog.close());
        buttonLayout.add(createButton, cancelButton);

        createRandomNetworkDialog.add(layout, buttonLayout);
        createRandomNetworkDialog.open();
    }

    private static void showAboutDialog() {
        Dialog aboutDialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("藕丝：一款自由的社会网络分析软件。"));
        layout.add(new Button("OK", event -> aboutDialog.close()));
        aboutDialog.add(layout);
        aboutDialog.open();
    }
}

