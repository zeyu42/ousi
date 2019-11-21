package org.ousi.ousi;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import de.wathoserver.vaadin.visjs.network.NetworkDiagram;
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
@SuppressWarnings("serial")
@Route("")
//@PWA(name = "Ousi", shortName = "Ousi")
public class MainView extends AppLayout {

    private Ousi ousi = new Ousi();
    private SplitLayout leftLayout = new SplitLayout();
    private VerticalLayout rightLayout = new VerticalLayout();
    private Grid<Network> networkGrid = new Grid<>();
    private Network network1 = null;
    private NetworkDiagram networkDiagram1 = null;
    private Network network2 = null;
    private NetworkDiagram networkDiagram2 = null;
    private SplitLayout visualizeSplitLayout = new SplitLayout(); // Use this only when user displays >1 graphs

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
        fileSubMenu.addItem("Settings", event -> showSettingsDialog());

        // Generate...
        MenuItem generateMenuItem = menu.addItem("Generate");
        SubMenu generateSubMenu = generateMenuItem.getSubMenu();
        generateSubMenu.addItem("Complete Network", event -> showCreateCompleteNetworkDialog());
        generateSubMenu.addItem("Random Network", event -> showCreateRandomNetworkDialog());

        // Visualize...
        MenuItem visualizeMenuItem = menu.addItem("Visualize");
        SubMenu visualizeSubMenu = visualizeMenuItem.getSubMenu();
        visualizeSubMenu.addItem("One pane", event -> {
            try {
                leftLayout.remove(leftLayout.getPrimaryComponent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            leftLayout.addToPrimary(networkDiagram1);
        });
        visualizeSubMenu.addItem("Two panes", event -> {
            try {
                leftLayout.remove(leftLayout.getPrimaryComponent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            leftLayout.addToPrimary(visualizeSplitLayout);
        });

        // Transform...
        MenuItem transformMenuItem = menu.addItem("Transform");
        SubMenu transformSubMenu = transformMenuItem.getSubMenu();
        transformSubMenu.addItem("Add", event -> add());
        transformSubMenu.addItem("Filter edge", event -> showFilterEdgeDialog());

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

        leftLayout.setOrientation(SplitLayout.Orientation.VERTICAL);

        Accordion outputAccordion = new Accordion();
        ousi.setOutputAccordion(outputAccordion);
        leftLayout.addToPrimary(networkDiagram1);
        leftLayout.addToSecondary(outputAccordion);
        leftLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

        visualizeSplitLayout.addToPrimary(networkDiagram1);
        visualizeSplitLayout.addToSecondary(networkDiagram2);

        networkGrid.setItems(ousi.getNetworks());
        networkGrid.addColumn(Network::getLabel).setHeader("Label");
        networkGrid.addColumn(Network::getDescription).setHeader("Description");
        networkGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridContextMenu<Network> contextMenu = networkGrid.addContextMenu();
        contextMenu.addItem("Download Binary", this::downloadNetworkBinary);
        contextMenu.addItem("Download DOT", this::downloadNetworkDOT);
        contextMenu.addItem("Remove", this::removeNetwork);
        contextMenu.addItem("Visualize", this::visualizeNetwork);

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

    private void showFilterEdgeDialog() {
        Dialog filterEdgeDialog = new Dialog();
        TextField thresholdTextField = new TextField("Threshold");
        thresholdTextField.setPlaceholder("5");
        Button OKButton = new Button("OK", event -> {
            double threshold;
            if (thresholdTextField.getValue().equals("")) {
                threshold = Double.parseDouble(thresholdTextField.getPlaceholder());
            } else {
                threshold = Double.parseDouble(thresholdTextField.getValue());
            }
            filterEdge(threshold);
            filterEdgeDialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> filterEdgeDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(OKButton, cancelButton);
        filterEdgeDialog.add(thresholdTextField, buttonLayout);
        filterEdgeDialog.open();
    }

    private void filterEdge(double threshold) {
        if (networkGrid.getSelectedItems().size() == 1) {
            Network unfiltered = null;
            for (Network network : networkGrid.getSelectedItems()) {
                unfiltered = network;
            }
            assert unfiltered != null;
            if (unfiltered.getHasWeight()) {
                Network filtered = new Network(unfiltered.getIsDirected(), true);
                for (Vertex vertex : unfiltered.getVertices()) {
                    for (Edge edge : unfiltered.getEdges(vertex)) {
                        if (edge.getWeight() >= threshold) {
                            filtered.addEdge(edge);
                        }
                    }
                }
                filtered.setLabel(unfiltered.getLabel() + "f");
                ousi.addNetwork(filtered, "Transform -> Filter edge", "Filtered edge with threshold " + threshold + ". Output: " + filtered.getLabel());
                networkGrid.getDataProvider().refreshAll();
            } else {
                Notification.show("Please select a weighted network.");
            }
        } else {
            Notification.show("Please select exactly one network.");
        }
    }

    private void add() {
        if (networkGrid.getSelectedItems().size() > 1) {
            boolean isDirected = true; // value unimportant
            boolean hasWeight = false; // value unimportant
            boolean first = true;
            for (Network network : networkGrid.getSelectedItems()) {
                if (first) {
                    isDirected = network.getIsDirected();
                    hasWeight = network.getHasWeight();
                } else {
                    first = false;
                    if (isDirected != network.getIsDirected() || hasWeight != network.getHasWeight()) {
                        Notification.show("Please make sure that all graphs are directed/undirected and weighted/unweighted.");
                    }
                }
            }
            Network sum = new Network(isDirected, hasWeight);
            // Add vertices
            for (Network network : networkGrid.getSelectedItems()) {
                for (Vertex vertex : network.getVertices()) {
                    if (!sum.containsVertex(vertex)) {
                        sum.addVertex(vertex);
                    }
                }
            }
            // Add edges
            for (Network network : networkGrid.getSelectedItems()) {
                for (Vertex vertex : network.getVertices()) {
                    for (Edge edge : network.getEdges(vertex)) {
                        if (!sum.getEdges(vertex).contains(edge)) {
                            sum.addEdge(edge);
                        }
                    }
                }
            }
            // Generate label
            StringBuilder label = new StringBuilder();
            first = true;
            for (Network network : networkGrid.getSelectedItems()) {
                if (!first) {
                    label.append(" + ");
                } else {
                    first = false;
                }
                label.append(network.getLabel());
            }
            sum.setLabel(label.toString());
            ousi.addNetwork(sum, "Transform -> Add", "Addition done. Output: " + label.toString());
            networkGrid.getDataProvider().refreshAll();
        } else {
            Notification.show("Please selected at least two networks.");
        }
    }

    private void downloadNetworkDOT(GridContextMenu.GridContextMenuItemClickEvent<Network> networkGridContextMenuItemClickEvent) {
        if (networkGrid.getSelectedItems().size() == 0) {
            Optional<Network> item = networkGridContextMenuItemClickEvent.getItem();
            if (!item.isPresent()) {
                return;
            }
            Network network = item.get();
            downloadSingleNetworkDOT(network);
        } else {
            for (Network network : networkGrid.getSelectedItems()) {
                downloadSingleNetworkDOT(network);
            }
        }
    }

    private void downloadSingleNetworkDOT(Network network) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Objects.requireNonNull(network.toDOT().getBytes()));
        LazyDownloadButton downloadButton = new LazyDownloadButton("Download", () -> network.getLabel().replace(" ", "").replace("+", "_") + ".gv", () -> byteArrayInputStream);
        downloadButton.setVisible(false);
        rightLayout.add(downloadButton);
        downloadButton.click();
    }

    private void visualizeNetwork(GridContextMenu.GridContextMenuItemClickEvent<Network> networkGridContextMenuItemClickEvent) {
        if (networkGrid.getSelectedItems().size() == 0) {
            Optional<Network> item = networkGridContextMenuItemClickEvent.getItem();
            if (!item.isPresent()) {
                return;
            }
            Network network = item.get();
            visualizeSingleNetwork(network);
        } else if (networkGrid.getSelectedItems().size() == 1) {
            for (Network network : networkGrid.getSelectedItems()) {
                visualizeSingleNetwork(network);
            }
        } else if (networkGrid.getSelectedItems().size() == 2) {
            visualizeSplitLayout.removeAll();
            int count = 0;
            for (Network network : networkGrid.getSelectedItems()) {
                if (count == 0) {
                    network1 = network;
                    networkDiagram1 = network.getNetworkDiagram(false, ousi.getSettings());
                    networkDiagram1.diagramFit();
                } else {
                    network2 = network;
                    networkDiagram2 = network.getNetworkDiagram(false, ousi.getSettings());
                    networkDiagram2.diagramFit();
                }
                count++;
            }
            visualizeSplitLayout.addToPrimary(networkDiagram1);
            visualizeSplitLayout.addToSecondary(networkDiagram2);
        }
    }

    private void visualizeSingleNetwork(Network network) {
        if (leftLayout.getPrimaryComponent() == networkDiagram1) {
            // One pane
            network1 = network;
            try {
                leftLayout.remove(networkDiagram1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            networkDiagram1 = network.getNetworkDiagram(false, ousi.getSettings());
            networkDiagram1.diagramSetSize("300px", "300px");
            leftLayout.addToPrimary(networkDiagram1);
        } else {
            // Two panes
            if (networkDiagram1 == null) {
                network1 = network;
                try {
                    visualizeSplitLayout.remove(networkDiagram1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                networkDiagram1 = network.getNetworkDiagram(false, ousi.getSettings());
                networkDiagram1.diagramFit();
                visualizeSplitLayout.addToPrimary(networkDiagram1);
            } else {
                network2 = network;
                try {
                    visualizeSplitLayout.remove(networkDiagram2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                networkDiagram2 = network.getNetworkDiagram(false, ousi.getSettings());
                networkDiagram2.diagramFit();
                visualizeSplitLayout.addToSecondary(networkDiagram2);
            }
        }
    }

    private void showSettingsDialog() {
        Dialog settingsDialog = new Dialog();
        FormLayout layout = new FormLayout();
        Checkbox useDegreeThresholdCheckbox = new Checkbox("Use a threshold for visualization");
        useDegreeThresholdCheckbox.setValue(ousi.getSettings().getUseDegreeThreshold());
        NumberField degreeThresholdNumberField = new NumberField();
        degreeThresholdNumberField.setEnabled(ousi.getSettings().getUseDegreeThreshold());
        degreeThresholdNumberField.setValue((double) ousi.getSettings().getDegreeThreshold());
        useDegreeThresholdCheckbox.addValueChangeListener(event -> degreeThresholdNumberField.setEnabled(useDegreeThresholdCheckbox.getValue()));
        layout.add(useDegreeThresholdCheckbox, degreeThresholdNumberField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button OKButton = new Button("OK", event -> {
            ousi.getSettings().setUseDegreeThreshold(useDegreeThresholdCheckbox.getValue());
            ousi.getSettings().setDegreeThreshold(degreeThresholdNumberField.getValue().intValue());
            ousi.getSettings().writeSettings();
            networkDiagram1 = network1.getNetworkDiagram(true, ousi.getSettings());
            networkDiagram2 = network2.getNetworkDiagram(true, ousi.getSettings());
            settingsDialog.close();
        });
        OKButton.addClickShortcut(Key.ENTER);
        Button cancelButton = new Button("Cancel", event -> settingsDialog.close());
        buttonLayout.add(OKButton, cancelButton);

        settingsDialog.add(layout, buttonLayout);
        settingsDialog.open();
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

    private void downloadNetworkBinary(GridContextMenu.GridContextMenuItemClickEvent<Network> networkGridContextMenuItemClickEvent) {
        if (networkGrid.getSelectedItems().size() == 0) {
            Optional<Network> item = networkGridContextMenuItemClickEvent.getItem();
            if (!item.isPresent()) {
                return;
            }
            Network network = item.get();
            downloadSingleNetworkBinary(network);
        } else {
            for (Network network : networkGrid.getSelectedItems()) {
                downloadSingleNetworkBinary(network);
            }
        }
    }

    private void downloadSingleNetworkBinary(Network network) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Objects.requireNonNull(FileManager.networkBinaryBytes(network)));
        LazyDownloadButton downloadButton = new LazyDownloadButton("Download", () -> network.getLabel().replace(" ", "").replace("+", "_") + ".bin", () -> byteArrayInputStream);
        downloadButton.setVisible(false);
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

    private void showCreateCompleteNetworkDialog() {
        // If user's input is invalid, an exception will be thrown and the program will on running.
        Dialog createCompleteNetworkDialog = new Dialog();
        FormLayout layout = new FormLayout();
        TextField nTextField = new TextField();
        nTextField.setAutofocus(true);
        nTextField.setLabel("# of Nodes");
        nTextField.setPlaceholder("10");
        RadioButtonGroup<String> isDirectedRadioButtonGroup = new RadioButtonGroup<>();
        isDirectedRadioButtonGroup.setItems("Directed", "Undirected");
        isDirectedRadioButtonGroup.setValue("Directed");
        Checkbox hasWeightCheckbox = new Checkbox("Weighted");
        NumberField lowerBoundNumberField = new NumberField("Lower bound (inclusive)");
        lowerBoundNumberField.setPlaceholder("1");
        lowerBoundNumberField.setEnabled(false);
        NumberField upperBoundNumberField = new NumberField("Upper bound (exclusive)");
        upperBoundNumberField.setPlaceholder("10");
        upperBoundNumberField.setEnabled(false);
        hasWeightCheckbox.addValueChangeListener(event -> {
            lowerBoundNumberField.setEnabled(hasWeightCheckbox.getValue());
            upperBoundNumberField.setEnabled(hasWeightCheckbox.getValue());
        });

        layout.add(nTextField, isDirectedRadioButtonGroup, hasWeightCheckbox, lowerBoundNumberField, upperBoundNumberField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button createButton = new Button("Create", event -> {
            int n;
            if (nTextField.getValue().equals("")) {
                n = Integer.parseInt(nTextField.getPlaceholder());
            } else {
                n = Integer.parseInt(nTextField.getValue());
            }
            boolean isDirected = isDirectedRadioButtonGroup.getValue().equals("Directed");
            boolean hasWeight = hasWeightCheckbox.getValue();
            int lowerBound;
            if (lowerBoundNumberField.getValue() != null) {
                lowerBound = lowerBoundNumberField.getValue().intValue();
            } else {
                lowerBound = Integer.parseInt(lowerBoundNumberField.getPlaceholder());
            }
            int upperBound;
            if (upperBoundNumberField.getValue() != null) {
                upperBound = upperBoundNumberField.getValue().intValue();
            } else {
                upperBound = Integer.parseInt(upperBoundNumberField.getPlaceholder());
            }
            ousi.createCompleteNetwork(n, isDirected, hasWeight, lowerBound, upperBound);
            networkGrid.getDataProvider().refreshAll();
            createCompleteNetworkDialog.close();
        });
        createButton.addClickShortcut(Key.ENTER);
        Button cancelButton = new Button("Cancel", event -> createCompleteNetworkDialog.close());
        buttonLayout.add(createButton, cancelButton);

        createCompleteNetworkDialog.add(layout, buttonLayout);
        createCompleteNetworkDialog.open();
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
        RadioButtonGroup<String> isDirectedRadioButtonGroup = new RadioButtonGroup<>();
        isDirectedRadioButtonGroup.setItems("Directed", "Undirected");
        isDirectedRadioButtonGroup.setValue("Directed");
        Checkbox hasWeightCheckbox = new Checkbox("Weighted");
        NumberField lowerBoundNumberField = new NumberField("Lower bound (inclusive)");
        lowerBoundNumberField.setPlaceholder("1");
        lowerBoundNumberField.setEnabled(false);
        NumberField upperBoundNumberField = new NumberField("Upper bound (exclusive)");
        upperBoundNumberField.setPlaceholder("10");
        upperBoundNumberField.setEnabled(false);
        hasWeightCheckbox.addValueChangeListener(event -> {
            lowerBoundNumberField.setEnabled(hasWeightCheckbox.getValue());
            upperBoundNumberField.setEnabled(hasWeightCheckbox.getValue());
        });

        layout.add(nTextField, pTextField, isDirectedRadioButtonGroup, hasWeightCheckbox, lowerBoundNumberField, upperBoundNumberField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button createButton = new Button("Create", event -> {
            int n;
            if (nTextField.getValue().equals("")) {
                n = Integer.parseInt(nTextField.getPlaceholder());
            } else {
                n = Integer.parseInt(nTextField.getValue());
            }
            double p;
            if (pTextField.getValue().equals("")) {
                p = Double.parseDouble(pTextField.getPlaceholder());
            } else {
                p = Double.parseDouble(pTextField.getValue());
            }
            boolean isDirected = isDirectedRadioButtonGroup.getValue().equals("Directed");
            boolean hasWeight = hasWeightCheckbox.getValue();
            int lowerBound;
            if (lowerBoundNumberField.getValue() != null) {
                lowerBound = lowerBoundNumberField.getValue().intValue();
            } else {
                lowerBound = Integer.parseInt(lowerBoundNumberField.getPlaceholder());
            }
            int upperBound;
            if (upperBoundNumberField.getValue() != null) {
                upperBound = upperBoundNumberField.getValue().intValue();
            } else {
                upperBound = Integer.parseInt(upperBoundNumberField.getPlaceholder());
            }
            ousi.createRandomNetwork(n, p, isDirected, hasWeight, lowerBound, upperBound);
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
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.addSucceededListener(finishedEvent -> {
            Network network = FileManager.loadNetworkBinary(memoryBuffer.getInputStream());
            ousi.addNetwork(network, "File -> Open", "Load network with label " + network.getLabel() + ".");
            networkGrid.getDataProvider().refreshAll();
            openDialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> openDialog.close());
        openDialog.add(upload, cancelButton);
        openDialog.open();
    }
}

