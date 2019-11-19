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
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.stefan.LazyDownloadButton;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


/**
 * The main view contains a button and a click listener.
 */
@Route("")
@PWA(name = "Ousi", shortName = "Ousi")
public class MainView extends AppLayout {

    private Ousi ousi = new Ousi();
    private VerticalLayout rightLayout = new VerticalLayout();
    private Grid<Network> networkGrid = new Grid<>();

    public MainView() {
        // Navigation Bar
        // Logo
        Image logoImage = new Image(streamResource(), "Ousi Logo");
        logoImage.setHeight("44px");
        // Menu Bar
        MenuBar menu = new MenuBar();
        // File...
        MenuItem fileMenuItem = menu.addItem("File");
        SubMenu fileSubMenu = fileMenuItem.getSubMenu();
        fileSubMenu.addItem("Open", event -> showOpenDialog());
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
        analyzeSubMenu.addItem("Density", event -> computeDensity());

        // Help...
        MenuItem helpMenuItem = menu.addItem("Help");
        SubMenu helpSubMenu = helpMenuItem.getSubMenu();
        helpSubMenu.addItem("Help");
        helpSubMenu.addItem("About", event -> showAboutDialog());

        addToNavbar(true, logoImage, menu);

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
        networkGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridContextMenu<Network> contextMenu = networkGrid.addContextMenu();
        GridMenuItem<Network> downloadMenuItem = contextMenu.addItem("Download", this::downloadNetwork);
        GridMenuItem<Network> removeMenuItem = contextMenu.addItem("Remove", this::removeNetwork);

        rightLayout.add(networkGrid);

        mainLayout.addToPrimary(leftLayout);
        mainLayout.addToSecondary(rightLayout);
        mainLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

        mainLayout.setSizeFull();
        leftLayout.setSizeFull();
        networkGrid.setSizeFull();
        leftLayout.setSplitterPosition(62);
        mainLayout.setSplitterPosition(62);

        setContent(mainLayout);
    }


    private void computeDensity() {
        for (Network network : networkGrid.getSelectedItems()) {
            ousi.addToAccordion("Analyze -> Density", Analyzer.densityString(network));
        }
    }

    private void removeNetwork(GridContextMenu.GridContextMenuItemClickEvent<Network> networkGridContextMenuItemClickEvent) {
        if (networkGrid.getSelectedItems().size() == 0) {
            Optional<Network> item = networkGridContextMenuItemClickEvent.getItem();
            if (!item.isPresent()) {
                return;
            }
            Network network = item.get();
            removeSingleNetwork(network);
        } else {
            for (Network network : networkGrid.getSelectedItems()) {
                removeSingleNetwork(network);
            }
        }
    }

    private void removeSingleNetwork(Network network) {
        ousi.removeNetwork(network);
        networkGrid.getDataProvider().refreshAll();
    }

    private void downloadNetwork(GridContextMenu.GridContextMenuItemClickEvent<Network> networkGridContextMenuItemClickEvent) {
        if (networkGrid.getSelectedItems().size() == 0) {
            Optional<Network> item = networkGridContextMenuItemClickEvent.getItem();
            if (!item.isPresent()) {
                return;
            }
            Network network = item.get();
            downloadSingleNetwork(network);
        } else {
            for (Network network : networkGrid.getSelectedItems()) {
                downloadSingleNetwork(network);
            }
        }
    }

    private void downloadSingleNetwork(Network network) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Objects.requireNonNull(FileManager.networkBinaryBytes(network)));
        LazyDownloadButton downloadButton = new LazyDownloadButton("Download", () -> network.getLabel() + ".bin", () -> byteArrayInputStream);
        downloadButton.setVisible(false);
//        downloadButton.addDownloadStartsListener(event1 -> rightLayout.remove(downloadButton));
        rightLayout.add(downloadButton);
        downloadButton.click();
    }


    private static StreamResource streamResource() {
        try {
            File file = new File("src/main/webapp/img/logo.png");
            FileInputStream fileInputStream = new FileInputStream(file);
            return new StreamResource("logo.png", () -> {
                try {
                    byte[] bytes = new byte[(int) file.length()];
                    int n = fileInputStream.read(bytes);
                    if (n == 0) {
                        throw new IOException();
                    }
                    return new ByteArrayInputStream(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    private void showOpenDialog() {
        Dialog openDialog = new Dialog();
        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);
        upload.addFinishedListener(finishedEvent -> {
            String filename = finishedEvent.getFileName();
            ousi.addNetwork(FileManager.loadNetworkBinary(fileBuffer.getInputStream()));
            openDialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> openDialog.close());
        openDialog.add(upload, cancelButton);
        openDialog.open();
    }
}

