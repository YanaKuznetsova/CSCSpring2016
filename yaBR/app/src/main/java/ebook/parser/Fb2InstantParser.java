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
import java.util.regex.*;

import ebook.EBook;
import ebook.Person;

/**
 * FB2InstantParser
 */
class Fb2InstantParser {
	private final int MAX_XMLINFO_SIZE = 80;
	private final int MAX_FB2_SIZE = 20971520;
	private EBook eBook;
	private String source;
	private InputStream input;

	Fb2InstantParser(EBook eBook, InputStream input) throws IOException {
		this.eBook = eBook;
		this.source = this.createSource(input);
		this.input = input;
	}

	private String createSource(InputStream stream) throws IOException,
			NullPointerException {
		byte[] buffer = readInputStream(stream);
		this.eBook.encoding = this.getXmlEncoding(buffer);
		String preparedInput = new String(buffer, this.eBook.encoding);
		Matcher matcher =  SOP.fb2Annotation.matcher(preparedInput);
		if (matcher.find()) {
			this.eBook.annotation = matcher.group(1);
			preparedInput = matcher.replaceFirst("");
		}
		return preparedInput;
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

	private Person extractPerson(String input) {
		Matcher matcher;
		Person person = new Person();
		matcher = SOP.fb2FirstName.matcher(input);
		if (matcher.find())
			person.firstName = matcher.group(1).trim();
		matcher = SOP.fb2MiddleName.matcher(input);
		if (matcher.find())
			person.middleName = matcher.group(1).trim();
		matcher = SOP.fb2LastName.matcher(input);
		if (matcher.find())
			person.lastName = matcher.group(1).trim();
		return person;
	}

	protected void parse() {
		Matcher matcher;
		matcher = SOP.fb2Author.matcher(source);
		while (matcher.find())
			this.eBook.authors.add(extractPerson(matcher.group(1)));
		matcher = SOP.fb2Title.matcher(source);
		if (matcher.find())
			this.eBook.title = matcher.group(1);
		matcher = SOP.fb2genre.matcher(source);
		while (matcher.find())
			this.eBook.fb2Genres.add(matcher.group(1));
		matcher = SOP.fb2Language.matcher(source);
		if (matcher.find())
			this.eBook.language = matcher.group(1);
		matcher = SOP.fb2Sequence.matcher(source);
		if (matcher.find()) {
			String sequence = matcher.group(1);
			matcher = SOP.fb2SequenceName.matcher(sequence);
			if (matcher.find())
				this.eBook.sequenceName = matcher.group(1);
			matcher = SOP.fb2SequenceNumber.matcher(sequence);
			if (matcher.find())
				this.eBook.sequenceNumber = matcher.group(1);
		}
		matcher = SOP.fb2Body.matcher(source);
		if (matcher.find())			
			this.eBook.body = matcher.group(1).replaceAll("\\<[^>]*>","");

		if (eBook.doExtractCover) {
			matcher = SOP.fb2CoverName.matcher(source);
			if (matcher.find()) {
				matcher.group(1);
				this.eBook.cover = getCover();
			}
		}
		this.eBook.isOk = true;
	}

	private byte[] getCover() {
		byte[] buffer = new byte[MAX_FB2_SIZE];
		byte[] cover64;
		int amount = 0;
		int count = 0;
		try {
			while ((amount < MAX_FB2_SIZE) && (count != -1)) {
				count = this.input.read(buffer, amount, MAX_FB2_SIZE - amount);
				if (count != -1)
					amount += count;
			}
		} catch (IOException e) {
		}
		if (amount == MAX_FB2_SIZE) {
			return null;
		} else {
			int stop = -1;
			int start = -1;
			int counter = amount - 1;
			while (counter >= 0) {
				if (buffer[counter] == '<')
					if (buffer[counter + 1] == '/')
						if (buffer[counter + 2] == 'b')
							if (buffer[counter + 7] == 'y')
								if (buffer[counter + 8] == '>') {
									stop = counter - 1;
									break;
								}
				counter--;
			}
			while (counter >= 0) {
				if (buffer[counter] == '<')
					if (buffer[counter + 1] == 'b')
						if (buffer[counter + 3] == 'n')
							if (buffer[counter + 5] == 'r')
								if (buffer[counter + 6] == 'y') {
									start = counter;
									break;
								}
				counter--;
			}
			if ((start == -1) || (stop == -1)) {
				return null;
			}
			while (counter < stop) {
				if (buffer[counter] == '>') {
					start = counter + 1;
					break;
				}
				counter++;
			}
			int newSize = stop - start + 1;
			cover64 = new byte[newSize];
			System.arraycopy(buffer, start, cover64, 0, newSize);
		}
		return Base64Decoder.decode(cover64);
	}
}
