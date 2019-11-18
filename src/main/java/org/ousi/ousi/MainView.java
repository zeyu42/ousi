package org.ousi.ousi;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;



/**
 * The main view contains a button and a click listener.
 */
@Route("")
@PWA(name = "Ousi", shortName = "Ousi")
public class MainView extends AppLayout {

    public MainView() {
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        img.setHeight("44px");
        MenuBar menu = new MenuBar();
        MenuItem fileMenuItem = menu.addItem("File");
        SubMenu fileSubMenu = fileMenuItem.getSubMenu();
        fileSubMenu.addItem("Open");
        fileSubMenu.addItem("Save");
        fileSubMenu.addItem("Settings");

        MenuItem generateMenuItem = menu.addItem("Generate");
        SubMenu generateSubMenu = generateMenuItem.getSubMenu();
        generateSubMenu.addItem("Random graph");

        MenuItem transformMenuItem = menu.addItem("Transform");
        SubMenu transformSubMenu = transformMenuItem.getSubMenu();
        transformSubMenu.addItem("Add");

        MenuItem analyzeMenuItem = menu.addItem("Analyze");
        SubMenu analyzeSubMenu = analyzeMenuItem.getSubMenu();
        analyzeSubMenu.addItem("Density");

        MenuItem helpMenuItem = menu.addItem("Help");
        SubMenu helpSubMenu = helpMenuItem.getSubMenu();
        helpSubMenu.addItem("Help");
        helpSubMenu.addItem("About", event1 -> showAboutDialog());

        addToNavbar(true, img, menu);

        SplitLayout mainLayout = new SplitLayout();

        SplitLayout leftLayout = new SplitLayout();
        leftLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        SplitLayout visualizationLayout = new SplitLayout(); // Use this only when user displays >1 graphs
        TextArea outputTextArea = new TextArea();
        leftLayout.addToPrimary(visualizationLayout);
        leftLayout.addToSecondary(outputTextArea);
        leftLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

        Grid<Graph<Vertex, DefaultWeightedEdge>> graphGrid = new Grid<>();

        mainLayout.addToPrimary(leftLayout);
        mainLayout.addToSecondary(graphGrid);
        mainLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

        mainLayout.setSizeFull();
        leftLayout.setSizeFull();
        graphGrid.setSizeFull();
        leftLayout.setSplitterPosition(62);
        mainLayout.setSplitterPosition(62);

        setContent(mainLayout);
    }

    private static void showAboutDialog() {
        Dialog aboutDialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("藕丝：一款自由的社会网络分析软件。"));
        layout.add(new Button("OK", event2 -> aboutDialog.close()));
        aboutDialog.add(layout);
        aboutDialog.open();
    }
}

