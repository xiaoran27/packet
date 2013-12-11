package com.nikhaldimann.inieditor.test;

import junit.framework.*;
import com.nikhaldimann.inieditor.IniEditor;

import java.io.*;
import java.util.*;

public class IniEditorTest extends TestCase {

    public IniEditorTest(String name) {
        super(name);
    }

    /**
     * Adding sections.
     */
    public void testAddSection() {
        IniEditor i = new IniEditor();
        i.addSection("hallo");
        assertTrue(i.hasSection("hallo"));
        i.addSection("   HELLO\t ");
        assertTrue(i.hasSection("hello"));
    }

    /**
     * Adding duplicate sections.
     */
    public void testAddSectionDup() {
        IniEditor i = new IniEditor();
        assertTrue(i.addSection("hallo"));
        assertTrue(i.hasSection("hallo"));
           assertTrue(!i.addSection("HALLO"));
    }

    /**
     * Adding illegal sections.
     */
    public void testAddSectionIllegal() {
        IniEditor i = new IniEditor();
        try {
            i.addSection("[hallo");
            fail("Should throw IllegalArgumentException.");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("hallo]");
            fail("Should throw IllegalArgumentException.");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("  \t ");
            fail("Should throw IllegalArgumentException.");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("");
            fail("Should throw IllegalArgumentException.");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Checking for sections.
     */
    public void testHasSection() {
        IniEditor i = new IniEditor();
        i.addSection("HaLlO");
        assertTrue(i.hasSection("hAlLo"));
        assertTrue(i.hasSection(" hallo\t"));
        assertTrue(!i.hasSection("hello"));
    }

    /**
     * Removing sections.
     */
    public void testRemoveSection() {
        IniEditor i = new IniEditor("common");
        i.addSection("test");
        try {
            i.removeSection("common");
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        assertTrue(i.removeSection("test"));
        assertTrue(!i.hasSection("test"));
        assertTrue(!i.removeSection("bla"));
    }

    /**
     * Setting and getting options.
     */
    public void testSetGet() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        assertEquals(i.get("test", "hallo"), null);
        assertTrue(!i.hasOption("test", "hallo"));
        i.set(" \t TEST  ", " HALLO \t", " \tvelo ");
        assertEquals(i.get("test", "hallo"), "velo");
        assertTrue(i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bike");
        assertEquals(i.get(" TesT\t ", " \tHALLO "), "bike");
        assertTrue(i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bi\nk\n\re\n");
        assertEquals(i.get("test", "hallo"), "bike");
        assertTrue(i.hasOption("test", "hallo"));
        // with common section
        i = new IniEditor("common");
        i.addSection("test");
        assertEquals(i.get("common", "hallo"), null);
        assertEquals(i.get("test", "hallo"), null);
        assertTrue(!i.hasOption("test", "hallo"));
        i.set("common", "hallo", "velo");
        assertEquals(i.get("common", "hallo"), "velo");
        assertEquals(i.get("test", "hallo"), "velo");
        assertTrue(i.hasOption("common", "hallo"));
        assertTrue(!i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bike");
        assertEquals(i.get("test", "hallo"), "bike");
    }

    /**
     * Setting options with illegal names.
     */
    public void testSetIllegalName() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        try {
            i.set("test", "hallo=", "velo");
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", " \t\t ", "velo");
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", "", "velo");
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Setting options to inexistent section.
     */
    public void testSetNoSuchSection() {
        IniEditor i = new IniEditor();
        try {
            i.set("test", "hallo", "velo");
            fail("Should throw NoSuchSectionException");
        }
        catch (IniEditor.NoSuchSectionException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Setting and getting with null arguments.
     */
    public void testSetGetNull() {
        IniEditor i = new IniEditor();
           i.addSection("test");
        try {
            i.set(null, "hallo", "velo");
            fail("Should throw NullPointerException");
        }
        catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", null, "velo");
            fail("Should throw NullPointerException");
        }
        catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        i.set("test", "hallo", null);
        try {
            i.get(null, "hallo");
            fail("Should throw NullPointerException");
        }
        catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        try {
            i.get("test", null);
            fail("Should throw NullPointerException");
        }
        catch (NullPointerException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Removing options.
     */
    public void testRemoveOptions() {
        IniEditor i = new IniEditor();
           i.addSection("test");
           i.set("test", "hallo", "velo");
           assertTrue(i.hasOption("test", "hallo"));
           assertTrue(i.remove("test", "hallo"));
           assertEquals(i.get("test", "hallo"), null);
           assertTrue(!i.hasOption("test", "hallo"));
           assertTrue(!i.remove("test", "hallo"));
           try {
               i.remove("test2", "hallo");
               fail ("Should throw NoSuchSectionException");
           }
           catch (IniEditor.NoSuchSectionException ex) {
               /* ok, should happen */
           }
    }

    /**
     * Getting section names.
     */
    public void testSectionNames() {
        IniEditor i = new IniEditor();
           i.addSection("test");
           i.addSection("test2");
           List names = i.sectionNames();
           assertEquals(names.get(0), "test");
           assertEquals(names.get(1), "test2");
           assertEquals(names.size(), 2);
           // with common section
        i = new IniEditor("common");
           i.addSection("test");
           i.addSection("test2");
        names = i.sectionNames();
           assertTrue(names.contains("test") && names.contains("test2") && names.size() == 2);
    }

    /**
     * Getting option names.
     */
    public void testOptionNames() {
        IniEditor i = new IniEditor("common");
           i.addSection("test");
           i.set("test", "hallo", "velo");
           i.set("test", "hello", "bike");
           List names = i.optionNames("test");
           assertEquals(names.get(0), "hallo");
           assertEquals(names.get(1), "hello");
           assertEquals(names.size(), 2);
    }

    /**
     * Adding lines.
     */
    public void testAddLines() {
        IniEditor i = new IniEditor("common");
           i.addSection("test");
           i.addBlankLine("test");
           i.addComment("test", "hollderidi");
    }

    /**
     * Saving to a file.
     */
    public void testSave() throws IOException {
           String[] expected = new String[] {"[common]", "[test]", "", "hallo = velo", "# english",
                                             "hello = bike"};
        IniEditor i = new IniEditor("common");
           i.addSection("test");
           i.addBlankLine("test");
           i.set("test", "hallo", "velo");
           i.addComment("test", "english");
           i.set("test", "hello", "bike");
        File f = File.createTempFile("test", null);
        // with output stream
        i.save(new FileOutputStream(f));
        Object[] saved = fileToStrings(f);
        //System.out.println(Arrays.asList(saved));
        assertTrue(Arrays.equals(expected, saved));
        // with File
        i.save(f);
        saved = fileToStrings(f);
        assertTrue(Arrays.equals(expected, saved));
        // with file name
        i.save(f.toString());
        saved = fileToStrings(f);
        assertTrue(Arrays.equals(expected, saved));
    }

    /**
     * Saving and loading with a character encoding.
     */
    public void testSaveLoadCharset() throws IOException {
           String[] expected = new String[] {
               "[���������]", "[������]", "", "������  = ������"
        };
        String charsetName = "UTF-16";
        IniEditor i = new IniEditor("���������");
           i.addSection("������");
           i.set("������", "������", "������");
        File f = File.createTempFile("test", null);
        i.save(new OutputStreamWriter(new FileOutputStream(f), charsetName));
        i = new IniEditor("���������");
        i.load(new InputStreamReader(new FileInputStream(f), charsetName));
        assertEquals(i.get("������", "������"), "������");
    }

    /**
     * Closing file on load.
     */
    public void testLoadClosingStream() throws IOException {
        IniEditor i = new IniEditor();
           i.addSection("test");
           i.set("test", "hallo", "velo");
        File f = File.createTempFile("test", null);
        i.save(f.toString());
        i = new IniEditor();
        i.load(f);
        assertTrue(f.delete());
    }

    /**
     * Closing file on load.
     */
    public void testCaseSensitivity() throws IOException {
        IniEditor i = new IniEditor("Common", true);
           assertTrue(i.hasSection("Common"));
           assertTrue(!i.hasSection("common"));
           i.addSection("Test");
           assertTrue(i.hasSection("Test"));
           assertTrue(!i.hasSection("test"));
           i.set("Test", "Hallo", "velo");
           assertEquals("velo", i.get("Test", "Hallo"));
           assertNull(i.get("Test", "hallo"));
        try {
               i.set("TesT", "hallo", "velo");
               fail("should fail");
        }
        catch (IniEditor.NoSuchSectionException ex) {
            /* ok */
        }
    }

    public void testSetOptionFormat() throws IOException {
        IniEditor i = new IniEditor();
        try {
            i.setOptionFormatString("%s %s");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        i.setOptionFormatString("%s%s%s");
           i.addSection("test");
           i.set("test", "hallo", "velo");
        File f = File.createTempFile("test", null);
        i.save(new FileOutputStream(f));
        Object[] saved = fileToStrings(f);
        assertEquals("hallo=velo", saved[1]);
    }

    private static Object[] fileToStrings(File f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        List<String> l = new LinkedList<String>();
        while (r.ready()) {
            l.add(r.readLine());
        }
        return l.toArray();
    }

    private static File toTempFile(String text) throws IOException {
        File temp = File.createTempFile("inieditortest", null);
        FileWriter fw = new FileWriter(temp);
        fw.write(text);
        fw.close();
        return temp;
    }

    private static File toTempFile(String[] lines) throws IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i] + "\n");
        }
        return toTempFile(sb.toString());
    }

    public static Test suite() {
        return new TestSuite(IniEditorTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}