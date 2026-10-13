package RenameRtf;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

public class RenameRtf {
    // Change this prefix if another month is needed.
    private static final Pattern SERIAL = Pattern.compile(
    		 "Serial Number:[\\t \\u00a0]*([^\\s\\u00a0]+)");

    public static void main(String[] args) throws IOException {
        boolean dryRun = args.length == 1 && args[0].equals("--dry-run");
        if (args.length > 0 && !dryRun) {
            System.err.println("Usage: java RenameRtf [--dry-run]");
            return;
        }
        Path directory = Paths.get(".").toAbsolutePath().normalize();
        
        System.out.println("Using directory for RTF searching: " + directory);
        
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                        && file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".rtf")) {
                    files.add(file);
                }
            }
        }
        files.sort(Comparator.comparing(p -> p.getFileName().toString()));
        for (Path file : files) {
            try {
                rename(file, dryRun);
            } catch (Exception e) {
                System.err.println("ERROR " + file.getFileName() + ": " + e.getMessage());
            }
        }
    }

    private static void rename(Path source, boolean dryRun) throws Exception {
        BasicFileAttributes before = Files.readAttributes(source, BasicFileAttributes.class);
        RTFEditorKit kit = new RTFEditorKit();
        Document document = kit.createDefaultDocument();
        try (InputStream input = Files.newInputStream(source)) {
            kit.read(input, document, 0);
        }
        Matcher matcher = SERIAL.matcher(document.getText(0, document.getLength()));
        if (!matcher.find()) return;
        String serial = windowsName(matcher.group(1));
        String oldName = source.getFileName().toString();
        int underscore = oldName.indexOf('_');
        String suffix = underscore >= 0 ? oldName.substring(underscore)
                : oldName.substring(oldName.length() - 4);
        Path target = source.resolveSibling(serial + suffix);
        if (target.equals(source)) return;
        String newName = serial + suffix;
        if (oldName.equalsIgnoreCase(newName)) return;
        Path target1 = source.resolveSibling(newName);
        if (Files.exists(target1, LinkOption.NOFOLLOW_LINKS)) {
            System.err.println("SKIP (name exists): " + oldName + " -> " + target1.getFileName());
            return;
        }
        if (dryRun) {
            System.out.println("PREVIEW: " + oldName + " -> " + target1.getFileName());
            return;
        }
        // No REPLACE_EXISTING: never overwrite another file.
        Files.move(source, target1);
        try {
            BasicFileAttributes after = Files.readAttributes(target1, BasicFileAttributes.class);
            if (!before.lastModifiedTime().equals(after.lastModifiedTime())
                    || !before.creationTime().equals(after.creationTime())) {
                Files.getFileAttributeView(target1, BasicFileAttributeView.class).setTimes(
                        before.lastModifiedTime(), null, before.creationTime());
            }
            after = Files.readAttributes(target1, BasicFileAttributes.class);
            if (!before.lastModifiedTime().equals(after.lastModifiedTime())
                    || !before.creationTime().equals(after.creationTime())) {
                throw new IOException("File system did not preserve creation/modification times");
            }
        } catch (Exception failure) {
            try {
                Files.move(target1, source);
                Files.getFileAttributeView(source, BasicFileAttributeView.class).setTimes(
                        before.lastModifiedTime(), null, before.creationTime());
            } catch (Exception rollbackFailure) {
                failure.addSuppressed(rollbackFailure);
                System.err.println("Rollback failed; check: " + target1);
            }
            throw failure;
        }
        System.out.println("RENAMED: " + oldName + " -> " + target1.getFileName());
    }

    private static String windowsName(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            result.append(c < 32 || "<>:\"/\\|?*".indexOf(c) >= 0 ? 'x' : c);
        }
        for (int i = result.length() - 1; i >= 0; i--) {
            char c = result.charAt(i);
            if (c != '.' && c != ' ') break;
            result.setCharAt(i, 'x');
        }
        return result.toString();
    }
}