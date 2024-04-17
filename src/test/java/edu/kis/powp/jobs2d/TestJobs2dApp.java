package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.drivers.*;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.adapter.FlippingLineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.adapter.RotatingLineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.adapter.ScalingLineDriverAdapter;
import edu.kis.powp.jobs2d.events.*;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.RecordFeature;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        application.addTest("Load Compound Rectangle command", new SelectLoadCompoundRectangleCommandOptionListener());

        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());

        application.addTest("Load recorded command", new SelectLoadRecordedCommandOptionListener());

        application.addTest("Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));

    }
  
    private static void setupVisitorTest(Application application) {
        application.addTest("Show current command stats", new VisitorTest());
    }

    /**
     * Setup driver manager, and set default Job2dDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {
        Job2dDriver loggerDriver = new LoggerDriver(false);
        DriverFeature.addDriver("Simple Logger driver", loggerDriver);

        Job2dDriver loggerDriver2 = new LoggerDriver(true);
        DriverFeature.addDriver("Detailed Logger driver", loggerDriver2);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        Job2dDriver driver = new RecordingDriverDecorator(new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic"));
        DriverFeature.addDriver("Line Simulator with Recording Support", driver);
        DriverFeature.getDriverManager().setCurrentDriver(driver);

        driver = new RecordingDriverDecorator(new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special"));
        DriverFeature.addDriver("Special Line Simulator with Recording Support", driver);
        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", driver);

        driver = new LoggerDriver(false);
        UsageMonitorDriverDecorator usageMonitorDriver = new UsageMonitorDriverDecorator(driver);
        DriverFeature.addDriver("Usage monitor with logger", usageMonitorDriver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        UsageMonitorDriverDecorator usageMonitorDriver2 = new UsageMonitorDriverDecorator(driver);
        DriverFeature.addDriver("Special line Simulator with usage monitor", usageMonitorDriver2);

        DriversComposite driversComposite = new DriversComposite();
        driversComposite.addDriver(new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic"));
        driversComposite.addDriver(new LoggerDriver(true));
        DriverFeature.addDriver("BasicLine with Logger", driversComposite);

        Job2dDriver scalingDriver = new ScalingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic scaled x2", 2);
        DriverFeature.addDriver("Scaled x2 Line Simulator", scalingDriver);

        Job2dDriver flippingVerticallyDriver = new FlippingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "flip vertically", FlippingLineDriverAdapter.FLIP_VERTICALLY);
        DriverFeature.addDriver("Flip vertically Simulator", flippingVerticallyDriver);

        Job2dDriver flippingHorizontallyDriver = new FlippingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "flip horizontally", FlippingLineDriverAdapter.FLIP_HORIZONTALLY);
        DriverFeature.addDriver("Flip horizontally Simulator", flippingHorizontallyDriver);

        Job2dDriver flippingBothSidesDriver = new FlippingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "flip both sides", FlippingLineDriverAdapter.FLIP_BOTH_SIDES);
        DriverFeature.addDriver("Flip both sides Simulator", flippingBothSidesDriver);

        Job2dDriver rotating90DegClockDriver = new RotatingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "rotate 90 deg clockwise", RotatingLineDriverAdapter.ROTATE_90_CLOCKWISE);
        DriverFeature.addDriver("Rotate 90deg clockwise Simulator", rotating90DegClockDriver);

        Job2dDriver rotating90DegCounterClockDriver = new RotatingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "rotate 90 deg counterclockwise", RotatingLineDriverAdapter.ROTATE_90_COUNTERCLOCKWISE);
        DriverFeature.addDriver("Rotate 90deg counterclockwise Simulator", rotating90DegCounterClockDriver);

        Job2dDriver rotating180DegDriver = new RotatingLineDriverAdapter(drawerController, LineFactory.getBasicLine(), "rotate 180 deg", RotatingLineDriverAdapter.ROTATE_180);
        DriverFeature.addDriver("Rotate 180deg Simulator", rotating180DegDriver);

        DriverFeature.updateDriverInfo();
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getCommandManager());
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getCommandManager().getChangePublisher().addSubscriber(windowObserver);
    }

    /**
     * Setup menu for adjusting logging settings.
     *
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    private static void setupMouseHandler(Application application) {
        new MouseClickConverter(application.getFreePanel());
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");
                DrawerFeature.setupDrawerPlugin(app);
                CommandsFeature.setupCommandManager();
                RecordFeature.setupRecorderPlugin(app);
                DriverFeature.setupDriverPlugin(app);
                setupDrivers(app);
                setupPresetTests(app);
                setupCommandTests(app);
                setupVisitorTest(app);
                setupLogger(app);
                setupWindows(app);
                setupMouseHandler(app);

                app.setVisibility(true);
            }
        });
    }

}
