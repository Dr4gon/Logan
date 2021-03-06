package de.iteratec.logan.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.io.ByteStreams;


public class IOUtils {
  private static final String ZIP_EXTENSION     = "zip"; //$NON-NLS-1$
  private static final String GZ_EXTENSION      = "gz";  //$NON-NLS-1$
  private static final String TXT_DOT_EXTENSION = ".txt"; //$NON-NLS-1$

  public static File combineZipFileEntries(String zipFilePath) throws ZipException, IOException {
    File file = new File(zipFilePath);
    String destFileName = file.getName().replace("." + ZIP_EXTENSION, TXT_DOT_EXTENSION); //$NON-NLS-1$
    File destFile = new File(file.getParentFile(), destFileName);
    if (destFile.exists()) {
      return destFile;
    }

    BufferedOutputStream destinationStream = new BufferedOutputStream(new FileOutputStream(destFile));

    ZipFile zip = new ZipFile(file);
    List<ZipEntry> allEntries = getFileEntries(zip);
    List<ZipEntry> sortedEntries = Ordering.usingToString().sortedCopy(allEntries);
    for (ZipEntry zipEntry : sortedEntries) {
      BufferedInputStream fileEntryStream = new BufferedInputStream(zip.getInputStream(zipEntry));
      ByteStreams.copy(fileEntryStream, destinationStream);
      destinationStream.flush();
      fileEntryStream.close();
    }

    destinationStream.close();
    return destFile;
  }

  private static List<ZipEntry> getFileEntries(ZipFile zip) {
    Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
    List<ZipEntry> allEntries = Lists.newArrayList();
    while (zipFileEntries.hasMoreElements()) {
      ZipEntry entry = zipFileEntries.nextElement();
      if (!entry.isDirectory()) {
        allEntries.add(entry);
      }
    }
    return allEntries;
  }

  public static File combineGzFileEntries(String zipFilePath) throws ZipException, IOException {
    File file = new File(zipFilePath);
    String destFileName = file.getName().replace("." + GZ_EXTENSION, TXT_DOT_EXTENSION); //$NON-NLS-1$
    File destFile = new File(file.getParentFile(), destFileName);
    if (destFile.exists()) {
      return destFile;
    }

    BufferedOutputStream destinationStream = new BufferedOutputStream(new FileOutputStream(destFile));
    GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
    ByteStreams.copy(gzis, destinationStream);
    gzis.close();
    destinationStream.close();

    return destFile;
  }

  public static boolean isZipFile(String filePath) {
    return ZIP_EXTENSION.equalsIgnoreCase(getFileExtension(filePath));
  }

  public static boolean isGzFile(String filePath) {
    return GZ_EXTENSION.equalsIgnoreCase(getFileExtension(filePath));
  }

  private static String getFileExtension(String fileName) {
    String extension = null;

    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      extension = fileName.substring(i + 1);
    }

    return extension;
  }
}
