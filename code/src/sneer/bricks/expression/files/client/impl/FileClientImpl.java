package sneer.bricks.expression.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class FileClientImpl implements FileClient {

	private final CacheMap<Sneer1024, Download> _downloadsByHash = CacheMap.newInstance();

	@Override
	public Download startFileDownload(File file, Sneer1024 hashOfFile) {
		return startFileDownload(file, -1, hashOfFile);
	}


	@Override
	public Download startFileDownload(final File file, final long lastModified, final Sneer1024 hashOfFile) {
		return startDownload("file", file, hashOfFile, new Producer<Download>() { @Override public Download produce() throws RuntimeException {
			return my(Downloads.class).newFileDownload(file, lastModified, hashOfFile, new Closure() { @Override public void run() {
				removeDownload(hashOfFile);
			}});
		}});
	}


	@Override
	public Download startFolderDownload(File folder, Sneer1024 hashOfFolder) {
		return startFolderDownload(folder, -1, hashOfFolder);
	}


	@Override
	public Download startFolderDownload(final File folder, final long lastModified, final Sneer1024 hashOfFolder) {
		return startDownload("folder", folder, hashOfFolder, new Producer<Download>() { @Override public Download produce() throws RuntimeException {
			return my(Downloads.class).newFolderDownload(folder, lastModified, hashOfFolder, new Closure() { @Override public void run() {
				removeDownload(hashOfFolder);
			}});
		}});
	}


	@Override
	public int numberOfRunningDownloads() {
		return _downloadsByHash.size();
	}


	private Download startDownload(String type, File fileOrFolder, Sneer1024 hash, Producer<Download> downloadProducer) {
		my(Logger.class).log("Downloading {}: {} Hash:", type, fileOrFolder, hash);
		return _downloadsByHash.get(hash, downloadProducer);
	}


	private void removeDownload(Sneer1024 hash) { 
		_downloadsByHash.remove(hash);
	}

}
