/*
 * Copyright (C) 2011 Andrew Mochalov <avmae@mail.ru>
 * 
 *  This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 */
package ebook.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ebook.EBook;
import ebook.Person;

/**
 * EpubInstantParser
 */
class EpubInstantParser {
	private final int MAX_EPUBINFO_SIZE = 4096;
	private final int MAX_XMLINFO_SIZE = 80;
	private EBook eBook;
	private String source;
	private ZipFile zipFile;
	private Enumeration<? extends ZipEntry> zipEntries;

	EpubInstantParser(EBook eBook) throws IOException {
		this.eBook = eBook;
		this.source = this.createSource();
	}

	private String createSource() throws IOException {
		ZipEntry entry = null;
		zipFile = new ZipFile(this.eBook.fileName);
		zipEntries = zipFile.entries();
		while (zipEntries.hasMoreElements()) {
			entry = zipEntries.nextElement();
			if (entry.getName().matches("(?i).*\\.opf$"))
				break;
		}
		InputStream inputStream = zipFile.getInputStream(entry);
		byte[] buffer = readInputStream(inputStream);
		String encoding = getXmlEncoding(buffer);
		String preparedInput = new String(buffer, encoding);
		Matcher matcher = SOP.epubDescription.matcher(preparedInput);
		if (matcher.find()) {
			this.eBook.annotation = matcher.group(1);
			preparedInput = matcher.replaceFirst("");
		}
		return preparedInput;
	}
	
	private String readZipFilePart(String entryName) throws IOException {
		InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(entryName));
		byte[] buffer = readInputStream(inputStream);
		String encoding = getXmlEncoding(buffer);
		String preparedInput = new String(buffer, encoding);
		return preparedInput.replaceAll("\\<[^>]*>","");
	}

	private byte[] readInputStream(InputStream input) throws IOException {
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[1024];
	    while ((nRead = input.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }	 
	    buffer.flush();
	    return buffer.toByteArray();
	}

	private String getXmlEncoding(byte[] input) throws IOException {
		String encoding = null;
		String xmlHeader = new String(input, 0, MAX_XMLINFO_SIZE, "ISO-8859-1");
		Matcher matcher = SOP.xmlEncoding.matcher(xmlHeader.toString());
		if (matcher.find())
			encoding = matcher.group(1);
		else
			throw new IOException("Unknown encoding");
		return encoding;
	}

	protected void parse() throws IOException {
		Matcher matcher;
		matcher = SOP.epubTitle.matcher(this.source);
		if (matcher.find())
			this.eBook.title = matcher.group(1);
		matcher = SOP.epubAuthor.matcher(this.source);
		while (matcher.find()) {
			this.eBook.authors.add(new Person(matcher.group(1)));
		}
		matcher = SOP.epubLanguage.matcher(this.source);
		if (matcher.find())
			this.eBook.language = matcher.group(1);
		this.eBook.isOk = true;
		matcher = SOP.epubGenre.matcher(this.source);
		while (matcher.find())
			this.eBook.epubGenres.add(matcher.group(1));
		if (this.eBook.doExtractCover) {
			matcher = SOP.epubCover.matcher(this.source);
			if (matcher.find())
				this.eBook.cover = getCover(matcher.group(1));
		}
		
		matcher = SOP.epubManifest.matcher(this.source);
		if (matcher.find()) {
			String manifest = matcher.group(1);
			matcher = SOP.epubManifestItem.matcher(manifest);
			StringBuilder sb = new StringBuilder();
			while (matcher.find()) {
				sb.append(readZipFilePart(matcher.group(1)));
			}
			this.eBook.body = sb.toString();
		}
		
		this.eBook.isOk = true;
	}

	private byte[] getCover(String fileName) throws IOException {
		ZipEntry entry = null;
		zipEntries = zipFile.entries();
		while (zipEntries.hasMoreElements()) {
			entry = zipEntries.nextElement();
			if (entry.getName().matches("(?i).*" + fileName))
				break;
		}
		int fileLength = (int) entry.getSize();
		InputStream inputStream = zipFile.getInputStream(entry);
		byte[] output = new byte[fileLength];
		int counter = 0;
		int amount = 0;
		while (amount < fileLength) {
			counter = inputStream.read(output, amount, fileLength - amount);
			amount += counter;
		}
		return output;
	}
}
