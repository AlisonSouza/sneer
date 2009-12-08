package sneer.bricks.hardwaresharing.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileMapImpl implements FileMap {
	
	private final Map<Sneer1024, File>           _filesByHash   = new ConcurrentHashMap<Sneer1024, File>();
	private final Map<Sneer1024, FolderContents> _foldersByHash = new ConcurrentHashMap<Sneer1024, FolderContents>();	
	

	@Override
	public Sneer1024 putFolderContents(FolderContents contents) {
		Sneer1024 hash = my(Hasher.class).hash(contents);
		_foldersByHash.put(hash, contents);
		return hash; 
	}


	@Override
	public Sneer1024 put(File fileOrFolder, String... acceptedExtensions) throws IOException {
		my(Logger.class).log("Mapping " + fileOrFolder + fileSize(fileOrFolder));
		return (fileOrFolder.isDirectory())
			? putFolder(fileOrFolder, acceptedExtensions)
			: putFile(fileOrFolder);
	}


	private String fileSize(File fileOrFolder) {
		return fileOrFolder.isDirectory() ? "" : "(" + fileOrFolder.length() / 1024 + " KB)";
	}


	private Sneer1024 putFile(File file) throws IOException {
		Sneer1024 result = my(Hasher.class).hash(file);
		_filesByHash.put(result, file);
		return result;
	}

	private Sneer1024 putFolder(File folder, String... fileTypes) throws IOException {
		return putFolderContents(new FolderContents(immutable(putEachFolderEntry(folder, fileTypes))));
	}

	private List<FileOrFolder> putEachFolderEntry(File folder, String... fileTypes) throws IOException {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();

		for (File fileOrFolder : sortedFiles(folder, fileTypes))
			result.add(putFolderEntry(fileOrFolder, fileTypes));

		return result;
	}

	private FileOrFolder putFolderEntry(File fileOrFolder, String... fileTypes) throws IOException {
		Sneer1024 hashOfContents = put(fileOrFolder, fileTypes);

		return new FileOrFolder(fileOrFolder.getName(), fileOrFolder.lastModified(), hashOfContents, fileOrFolder.isDirectory());
	}
	private static ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
		return my(ImmutableArrays.class).newImmutableArray(entries);
	}
	
	private static File[] sortedFiles(File folder, final String... fileTypes) {
		File[] result = (fileTypes.length > 0)
			? folder.listFiles(new FileFilter() { @Override public boolean accept(File fileToBeAdded) {
				if (fileToBeAdded.isDirectory()) return true;
				final String fileExtension = my(Lang.class).strings().substringAfterLast(fileToBeAdded.getName(), ".").toLowerCase();
				if (Arrays.asList(fileTypes).contains(fileExtension)) return true;
				return false;
			}})
			: folder.listFiles();

		if (result == null)	return new File[0];

		Arrays.sort(result, new Comparator<File>() { @Override public int compare(File file1, File file2) {
			return file1.getName().compareTo(file2.getName());
		}});

		return result;
	}

	@Override public File getFile(Sneer1024 hash) {
		return _filesByHash.get(hash);
	}

	@Override public FolderContents getFolder(Sneer1024 hash) {
		return _foldersByHash.get(hash);
	}

}
