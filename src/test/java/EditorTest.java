import tk.mgdev.TextEditor;
import org.assertj.swing.core.ComponentLookupScope;
import org.assertj.swing.fixture.*;
import org.hyperskill.hstest.v6.stage.SwingTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestCase;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

class OsCheck {
    /**
     * types of Operating Systems
     */
    public enum OSType {
        Windows, MacOS, Linux, Other
    };

    // cached result of OS detection
    protected static OSType detectedOS;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty(
                "os.name", "generic")
                .toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac"))
                || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}


class TestClue {

    public Supplier<Boolean> function;
    public String feedback;

    public TestClue(String feedback, Supplier<Boolean> function) {
        this.function = function;
        this.feedback = feedback;
    }

}


public class EditorTest extends SwingTest<TestClue> {

    public EditorTest() {
        super(new TextEditor());
    }

    @Before
    public void fixLookup() {
        window.robot().settings()
            .componentLookupScope(ComponentLookupScope.ALL);
    }

    static boolean notWindows =
        OsCheck.getOperatingSystemType() != OsCheck.OSType.Windows;

    private String dir = System.getProperty("user.dir") + File.separator;
    private File fileDir = new File(dir);

    private JTextComponentFixture textArea;
    private JTextComponentFixture searchField;
    private JButtonFixture saveButton;
    private JButtonFixture openButton;
    private JButtonFixture startSearchButton;
    private JButtonFixture previousMatchButton;
    private JButtonFixture nextMatchButton;
    private JCheckBoxFixture useRegExCheckbox;
    private JScrollPaneFixture scrollPane;
    private JMenuItemFixture menuFile;
    private JMenuItemFixture menuSearch;
    private JMenuItemFixture menuOpen;
    private JMenuItemFixture menuSave;
    private JMenuItemFixture menuExit;
    private JMenuItemFixture menuStartSearch;
    private JMenuItemFixture menuPreviousMatch;
    private JMenuItemFixture menuNextMatch;
    private JMenuItemFixture menuUseRegExp;
    private JFileChooserFixture fileChooser;

    String filename1 = "SomeFile.txt";
    String filename2 = "AnotherFile.txt";
    String noExistFile = "FileDoesNotExist";

    String searchText = "Sonnet";
    String regExSearchText = "[fr]uel";

    String textToSave1 = "Basic text editor\nType here too\nHere also\n\n";
    String textToSave2 = "          Sonnet I\n" +
        "\n" +
        "\n" +
        "FROM fairest creatures we desire increase,\n" +
        "That thereby beauty's rose might never die,\n" +
        "But as the riper should by time decease,\n" +
        "His tender heir might bear his memory:\n" +
        "But thou, contracted to thine own bright eyes,\n" +
        "Feed'st thy light'st flame with self-substantial fuel,\n" +
        "Making a famine where abundance lies,\n" +
        "Thyself thy foe, to thy sweet self too cruel.\n" +
        "Thou that art now the world's fresh ornament\n" +
        "And only herald to the gaudy spring,\n" +
        "Within thine own bud buriest thy content\n" +
        "And, tender churl, makest waste in niggarding.\n" +
        "Pity the world, or else this glutton be,\n" +
        "To eat the world's due, by the grave and thee.\n" +
        "\n" +
        " Sonnet II\n" +
        "       \n" +
        "         \n" +
        "When forty winters shall beseige thy brow,\n" +
        "And dig deep trenches in thy beauty's field,\n" +
        "Thy youth's proud livery, so gazed on now,\n" +
        "Will be a tatter'd weed, of small worth held:\n" +
        "Then being ask'd where all thy beauty lies,\n" +
        "Where all the treasure of thy lusty days,\n" +
        "To say, within thine own deep-sunken eyes,\n" +
        "Were an all-eating shame and thriftless praise.\n" +
        "How much more praise deserved thy beauty's use,\n" +
        "If thou couldst answer 'This fair child of mine\n" +
        "Shall sum my count and make my old excuse,'\n" +
        "Proving his beauty by succession thine!\n" +
        "This were to be new made when thou art old,\n" +
        "And see thy blood warm when thou feel'st it cold.\n" +
        "\n" +
        "Sonnet III\n" +
        "\n" +
        "\n" +
        "Look in thy glass, and tell the face thou viewest\n" +
        "Now is the time that face should form another;\n" +
        "Whose fresh repair if now thou not renewest,\n" +
        "Thou dost beguile the world, unbless some mother.\n" +
        "For where is she so fair whose unear'd womb\n" +
        "Disdains the tillage of thy husbandry?\n" +
        "Or who is he so fond will be the tomb\n" +
        "Of his self-love, to stop posterity?\n" +
        "Thou art thy mother's glass, and she in thee\n" +
        "Calls back the lovely April of her prime:\n" +
        "So thou through windows of thine age shall see\n" +
        "Despite of wrinkles this thy golden time.\n" +
        "But if thou live, remember'd not to be,\n" +
        "Die single, and thine image dies with thee.\n" +
        "\n" +
        "Sonnet IV\n" +
        "\n" +
        "\n" +
        "Unthrifty loveliness, why dost thou spend\n" +
        "Upon thyself thy beauty's legacy?\n" +
        "Nature's bequest gives nothing but doth lend,\n" +
        "And being frank she lends to those are free.\n" +
        "Then, beauteous niggard, why dost thou abuse\n" +
        "The bounteous largess given thee to give?\n" +
        "Profitless usurer, why dost thou use\n" +
        "So great a sum of sums, yet canst not live?\n" +
        "For having traffic with thyself alone,\n" +
        "Thou of thyself thy sweet self dost deceive.\n" +
        "Then how, when nature calls thee to be gone,\n" +
        "What acceptable audit canst thou leave?\n" +
        "Thy unused beauty must be tomb'd with thee,\n" +
        "Which, used, lives th' executor to be.";

    public void fileAction() {
        if(!fileChooser.target().isVisible()) {
            throw new AssertionError();
        }
        frame.setVisible(false);
        fileChooser.setCurrentDirectory(fileDir);

        if (OsCheck.getOperatingSystemType() == OsCheck.OSType.MacOS) {
            fileChooser.selectFile(new File(
                fileDir + File.separator + searchField.text()));
        } else {
            fileChooser.fileNameTextBox().setText(searchField.text());
        }

        fileChooser.approve();
        frame.setVisible(true);
    }


    @Override
    public List<TestCase<TestClue>> generate() {
        return List.of(

            // general tests

            new TestCase<TestClue>().setAttach(new TestClue(
                "Title is empty",
                () -> frame.getTitle().length() > 0)),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Window is not visible",
                () -> frame.isVisible())),


            // existence tests

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no text component with name TextArea",
                () -> checkExistence(() -> {
                    textArea = window.textBox("TextArea");
                    return textArea;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no text component with name SearchField",
                () -> checkExistence(() -> {
                    searchField = window.textBox("SearchField");
                    return searchField;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name SaveButton",
                () -> checkExistence(() -> {
                    saveButton = window.button("SaveButton");
                    return saveButton;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name OpenButton",
                () -> checkExistence(() -> {
                    openButton = window.button("OpenButton");
                    return openButton;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name StartSearchButton",
                () -> checkExistence(() -> {
                    startSearchButton = window.button("StartSearchButton");
                    return startSearchButton;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name PreviousMatchButton",
                () -> checkExistence(() -> {
                    previousMatchButton = window.button("PreviousMatchButton");
                    return previousMatchButton;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name NextMatchButton",
                () -> checkExistence(() -> {
                    nextMatchButton = window.button("NextMatchButton");
                    return nextMatchButton;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no button with name UseRegExCheckbox",
                () -> checkExistence(() -> {
                    useRegExCheckbox = window.checkBox("UseRegExCheckbox");
                    return useRegExCheckbox;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no scroll component with name ScrollPane",
                () -> checkExistence(() -> {
                    scrollPane = window.scrollPane("ScrollPane");
                    return scrollPane;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuFile",
                () -> checkExistence(() -> {
                    menuFile = window.menuItem("MenuFile");
                    return menuFile;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuSearch",
                () -> checkExistence(() -> {
                    menuSearch = window.menuItem("MenuSearch");
                    return menuSearch;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuOpen",
                () -> checkExistence(() -> {
                    menuOpen = window.menuItem("MenuOpen");
                    return menuOpen;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuSave",
                () -> checkExistence(() -> {
                    menuSave = window.menuItem("MenuSave");
                    return menuSave;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuExit",
                () -> checkExistence(() -> {
                    menuExit = window.menuItem("MenuExit");
                    return menuExit;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuStartSearch",
                () -> checkExistence(() -> {
                    menuStartSearch = window.menuItem("MenuStartSearch");
                    return menuStartSearch;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuPreviousMatch",
                () -> checkExistence(() -> {
                    menuPreviousMatch = window.menuItem("MenuPreviousMatch");
                    return menuPreviousMatch;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuNextMatch",
                () -> checkExistence(() -> {
                    menuNextMatch = window.menuItem("MenuNextMatch");
                    return menuNextMatch;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no menu option with name MenuUseRegExp",
                () -> checkExistence(() -> {
                    menuUseRegExp = window.menuItem("MenuUseRegExp");
                    return menuUseRegExp;
                }))),

            new TestCase<TestClue>().setAttach(new TestClue(
                "There is no component with name FileChooser",
                () -> checkExistence(() -> {
                    fileChooser = window.fileChooser("FileChooser");
                    return fileChooser;
                }))),


            // ui tests

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should be editable",
                () -> {
                    textArea.requireEditable();
                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should be empty at the start of the program",
                () -> {
                    textArea.requireEmpty();
                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "SearchField should be empty at the start of the program",
                () -> {
                    searchField.requireEmpty();
                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "SaveButton should be enabled for clicking",
                () -> saveButton.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "OpenButton should be enabled for clicking",
                () -> openButton.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "StartSearchButton should be enabled for clicking",
                () -> startSearchButton.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "NextMatchButton should be enabled for clicking",
                () -> nextMatchButton.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "PreviousMatchButton should be enabled for clicking",
                () -> previousMatchButton.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuLoad should be enabled for clicking",
                () -> menuOpen.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuSave should be enabled for clicking",
                () -> menuSave.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuFile should be enabled for clicking",
                () -> menuFile.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuExit should be enabled for clicking",
                () -> menuExit.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuStartSearch should be enabled for clicking",
                () -> menuStartSearch.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuPreviousMatch should be enabled for clicking",
                () -> menuPreviousMatch.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuNextMatch should be enabled for clicking",
                () -> menuNextMatch.isEnabled())),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuUseRegExp should be enabled for clicking",
                () -> menuUseRegExp.isEnabled())),


            // logic tests

            new TestCase<TestClue>().setAttach(new TestClue(
                "FileChooser doesn't appear on the second " +
                    "press on SaveButton but should appear every time",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    searchField.setText(filename1);
                    textArea.setText(textToSave1);

                    saveButton.click();

                    try {
                        fileAction();
                    }
                    catch (IllegalStateException ex) {
                        return false;
                    }

                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Text should be the same after saving and loading same file",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    String[] texts = {textToSave2, textToSave1};
                    String[] files = {filename1, filename2};

                    for (int i = 0; i < 2; i++) {

                        String text = texts[i];
                        String file = files[i];

                        searchField.setText("");
                        textArea.setText("");

                        searchField.setText(file);
                        textArea.setText(text);

                        saveButton.click();
                        fileAction();

                        searchField.setText("");
                        textArea.setText("");

                        searchField.setText(file);
                        openButton.click();
                        fileAction();

                        textArea.requireText(text);
                    }

                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should be empty if user tries to " +
                    "load file that doesn't exist",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    textArea.setText(textToSave1);
                    searchField.setText(noExistFile);

                    openButton.click();
                    fileAction();
                    textArea.requireText("");

                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should correctly save and load an empty file",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    textArea.setText("");
                    searchField.setText(filename1);

                    saveButton.click();
                    fileAction();
                    textArea.setText(textToSave2);
                    openButton.click();
                    fileAction();
                    textArea.requireText("");

                    return true;
                })),



            // menu-related tests for save and load

            new TestCase<TestClue>().setAttach(new TestClue(
                "Text should be the same after saving " +
                    "and loading same file using MenuLoad",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    String[] texts = {textToSave2, textToSave1};
                    String[] files = {filename1, filename2};

                    for (int i = 0; i < 2; i++) {

                        String text = texts[i];
                        String file = files[i];

                        searchField.setText("");
                        textArea.setText("");

                        searchField.setText(file);
                        textArea.setText(text);

                        menuSave.click();
                        fileAction();

                        searchField.setText("");
                        textArea.setText("");

                        searchField.setText(file);
                        menuOpen.click();
                        fileAction();

                        textArea.requireText(text);
                    }

                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should be empty if user tries to " +
                    "load file that doesn't exist using MenuLoad",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    textArea.setText(textToSave1);
                    searchField.setText(noExistFile);

                    menuOpen.click();
                    fileAction();
                    textArea.requireText("");

                    return true;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "TextArea should correctly save " +
                    "and load an empty file using menu",
                () -> {
                    if (notWindows) {
                        return true;
                    }

                    textArea.setText("");
                    searchField.setText(filename1);

                    menuSave.click();
                    fileAction();
                    textArea.setText(textToSave2);
                    menuOpen.click();
                    fileAction();
                    textArea.requireText("");

                    return true;
                })),


            // search related tests

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking SearchButton should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    startSearchButton.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking NextMatchButton should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    startSearchButton.click();
                    nextMatchButton.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking PreviousMatchButton should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    startSearchButton.click();
                    previousMatchButton.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking MenuStartSearch should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    menuStartSearch.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking MenuNextMatch should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    startSearchButton.click();
                    menuNextMatch.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "After clicking MenuPreviousMatch should " +
                    "be selected founded text",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);
                    startSearchButton.click();
                    menuPreviousMatch.click();

                    return Objects.equals(
                        textArea.target().getSelectedText(), searchText);
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking SearchButton " +
                    "(should be at the end of founded text)",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    startSearchButton.click();

                    // confirmed with working program AND provided text
                    return textArea.target().getCaretPosition() == 16;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking SearchButton " +
                    "(should be at the end of founded text)",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    menuStartSearch.click();

                    // confirmed with working program AND provided text
                    return textArea.target().getCaretPosition() == 16;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking NextMatchButton",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    startSearchButton.click();
                    nextMatchButton.click();

                    // confirmed with working program
                    return textArea.target().getCaretPosition() == 640;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking MenuNextMatch",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    menuStartSearch.click();
                    menuNextMatch.click();

                    // confirmed with working program
                    return textArea.target().getCaretPosition() == 640;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking PreviousMatchButton",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    startSearchButton.click();
                    previousMatchButton.click();

                    // confirmed with working program
                    return textArea.target().getCaretPosition() == 1921;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking MenuPreviousMatch",
                () -> {
                    searchField.setText(searchText);
                    textArea.setText(textToSave2);

                    startSearchButton.click();
                    menuPreviousMatch.click();

                    // confirmed with working program
                    return textArea.target().getCaretPosition() == 1921;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Checkbox is not clickable",
                () -> {
                    useRegExCheckbox.check(true);
                    return useRegExCheckbox.target().isSelected();
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "MenuUseRegExp does't work",
                () -> {
                    useRegExCheckbox.check(false);
                    menuUseRegExp.click();
                    return useRegExCheckbox.target().isSelected();
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking SearchButton " +
                    "using regular expression",
                () -> {
                    searchField.setText(regExSearchText);
                    textArea.setText(textToSave2);

                    startSearchButton.click();

                    // confirmed with working program AND provided text
                    return textArea.target().getCaretPosition() == 288;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking NextSearchButton " +
                    "using regular expression",
                () -> {
                    searchField.setText(regExSearchText);
                    textArea.setText(textToSave2);

                    menuStartSearch.click();
                    nextMatchButton.click();

                    // confirmed with working program AND provided text
                    return textArea.target().getCaretPosition() == 372;
                })),

            new TestCase<TestClue>().setAttach(new TestClue(
                "Wrong caret position after clicking PreviousMatchButton " +
                    "using regular expression",
                () -> {
                    searchField.setText(regExSearchText);
                    textArea.setText(textToSave2);

                    menuStartSearch.click();
                    previousMatchButton.click();

                    // confirmed with working program AND provided text
                    return textArea.target().getCaretPosition() == 372;
                }))
        );
    }

    @Override
    public CheckResult check(String reply, TestClue clue) {
        try {
            return new CheckResult(clue.function.get(), clue.feedback);
        }
        catch (AssertionError ex) {
            return new CheckResult(false, clue.feedback);
        }
    }

    @After
    public void deleteFiles() {
        try {
            Files.deleteIfExists(Paths.get(filename1));
            Files.deleteIfExists(Paths.get(filename2));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
