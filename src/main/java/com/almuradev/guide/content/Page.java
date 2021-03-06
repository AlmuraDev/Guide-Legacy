/*
 * This file is part of Guide, licensed under the MIT License (MIT).
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev/Guide/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.almuradev.guide.content;

import java.util.Comparator;
import java.util.Date;

public final class Page {

    private final String identifier;
    private Date created, lastModified;
    private String name, author, lastContributor, contents;
    private int index;

    public Page(String identifier, int index, String name, Date created, String author, Date lastModified, String lastContributor, String contents) {
        this.identifier = identifier;
        this.index = index;
        this.name = name;
        this.created = created;
        this.author = author;
        this.lastModified = lastModified;
        this.lastContributor = lastContributor;
        this.contents = contents;
    }

    public int getIndex() {
        return index;
    }

    public Page setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Date getCreated() {
        return created;
    }

    public Page setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Page setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getName() {
        return name;
    }

    public Page setTitle(String name) {
        this.name = name;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Page setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public Page setLastContributor(String lastContributor) {
        this.lastContributor = lastContributor;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Page setContents(String contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && identifier.equals(((Page) o).identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "Page { " +
                "identifier= [" + identifier + "]" +
                ", index= [" + index + "]" +
                ", created= [" + created + "]" +
                ", lastModified= [" + lastModified + "]" +
                ", title= [" + name + "]" +
                ", author= [" + author + "]" +
                ", lastContributor= [" + lastContributor + "]" +
                ", contents= [" + contents + "]" +
                " }";
    }

    public static class PageIndexComparator implements Comparator<Page> {

        @Override
        public int compare(Page o1, Page o2) {
            return Integer.compare(o1.index, o2.index);
        }
    }
}
