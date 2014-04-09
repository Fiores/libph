package com.cab404.libtabun.util.html_parser;

import com.cab404.libtabun.util.SU;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Простой навигатор по HTML.
 *
 * @author cab404
 */
public class HTMLTree implements Iterable<Tag> {

    int start = 0;
    int end = 0;

    private final List<LevelAnalyzer.LeveledTag> leveled;
    public final CharSequence html;

    @Override
    public Iterator<Tag> iterator() {
        return new Iterator<Tag>() {
            int i = 0;
            @Override public boolean hasNext() {
                return i < size();
            }
            @Override public Tag next() {
                return get(i++);
            }
            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    private int size() {
        return leveled.size();
    }

    public Tag get(int index) {
        return leveled.get(index).tag;
    }

    public int getLevel(int index) {
        return leveled.get(index).getLevel();
    }

    public int getLevel(Tag tag) {
        return getLevel(tag.index - start);
    }


    private HTMLTree(HTMLTree tree, int start, int end) {
        this.html = tree.html;
        this.start = start;
        this.end = end;

        this.leveled = tree.leveled.subList(start, end);
    }

    public HTMLTree(LevelAnalyzer analyzed, CharSequence data) {
        this(analyzed.getSlice(0, analyzed.size()), data);
    }

    public HTMLTree(List<LevelAnalyzer.LeveledTag> analyzed, CharSequence data) {
        html = data;
        leveled = analyzed;
        start = analyzed.get(0).tag.index;
        end = analyzed.get(analyzed.size() - 1).tag.index;
    }

    public HTMLTree(String text) {
        final LevelAnalyzer analyzer = new LevelAnalyzer(text);

        TagParser parser = new TagParser();

        parser.setTagHandler(new TagParser.TagHandler() {
            @Override public void handle(Tag tag) {
                analyzer.add(tag);
            }
        });

        parser.process(text);
        html = text;

        analyzer.fixLayout();
        leveled = analyzer.getSlice(0, analyzer.size());

        start = 0;
        end = leveled.size();
    }

    public List<Tag> getAllTagsByProperty(String key, String value) {
        ArrayList<Tag> _return = new ArrayList<>();
        for (Tag tag : this)
            if (value.equals(tag.props.get(key)))
                _return.add(tag);
        return _return;
    }

    public List<Tag> getAllTagsByName(String name) {
        ArrayList<Tag> _return = new ArrayList<>();
        for (Tag tag : this)
            if (tag.name.equals(name))
                _return.add(tag);
        return _return;
    }

    public Tag getTagByName(String name) {
        for (Tag tag : this)
            if (tag.name.equals(name))
                return tag;
        throw new TagNotFoundException();
    }

    public Tag getTagByProperty(String key, String value) {
        return get(getTagIndexByProperty(key, value));
    }

    public int getTagIndexByProperty(String key, String value) {
        for (Tag tag : this)
            if (value.equals(tag.props.get(key)))
                return getIndexForTag(tag);
        throw new TagNotFoundException();
    }

    private int getIndexForTag(Tag tag) {
        return tag.index - start;
    }

    public int getTagIndexForName(String name) {
        for (Tag tag : this)
            if (tag.name.equals(name))
                return getIndexForTag(tag);
        throw new TagNotFoundException();
    }

    public int getTagIndexForParamRegex(String key, String regex) {
        Pattern pattern = Pattern.compile(regex);
        for (Tag tag : this)
            if (tag.props.containsKey(key))
                if (pattern.matcher(tag.props.get(key)).matches())
                    return getIndexForTag(tag);
        throw new TagNotFoundException();
    }


    public int getClosingTag(Tag tag) {

        int index = getIndexForTag(tag) + 1, level = getLevel(tag);
        for (; index < end; index++) {

            Tag check = get(index);
            int c_level = getLevel(check);

            if (c_level == level && check.name.equals(tag.name))
                return getIndexForTag(check);

        }

        throw new TagNotFoundException();
    }

    public String getContents(int index) {
        return getContents(get(index));
    }

    public String getContents(Tag tag) {
        return html.subSequence(tag.end, get(getClosingTag(tag)).start).toString();
    }

//    public String getContents(int index) {
//        return getContents(get(index)).toString();
//    }
//
//    public String getContents(Tag tag) {
//        return html.subSequence(tag.end, get(getClosingTag(tag)).start).toString();
//    }


    /**
     * Возвращает уровень тегов целиком.
     */
    public HTMLTree getTree(Tag opening) {

        if (opening.isClosing()) throw new RuntimeException("Попытка достать парсер для закрывающего тега!");
        if (opening.isStandalone()) throw new RuntimeException("Попытка достать парсер для standalone-тега!");

        return new HTMLTree(this, opening.index, getClosingTag(opening) + 1);
    }

    /**
     * Возвращает уровень тегов целиком.
     */
    @Deprecated
    public HTMLTree getTree(int index) {
        return getTree(get(index));
    }

    public List<Tag> getTopChildren(Tag tag) {
        if (tag.isClosing()) throw new RuntimeException("Попытка достать теги верхнего уровня для закрывающего тега!");
        if (tag.isStandalone()) throw new RuntimeException("Попытка достать теги верхнего уровня для standalone-тега!");


        ArrayList<Tag> _return = new ArrayList<>();


        int index = getIndexForTag(tag) + 1, level = getLevel(tag);

        for (; index < end; index++) {

            Tag check = get(index);
            int c_level = getLevel(check);

            if (getLevel(check) - 1 == level)
                if (check.isOpening() || check.isStandalone())
                    _return.add(check);

            if (c_level == level)
                break;

        }

        return _return;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int shift = getLevel(0);

        for (Tag tag : this)
            out
                    .append(getLevel(tag) - shift)
                    .append(SU.tabs(getLevel(tag) - shift))
                    .append(tag)
                    .append("\n");
        return out.toString();
    }

    public static class TagNotFoundException extends RuntimeException {
        public TagNotFoundException() {
            super();
        }
    }

    public List<Tag> copyList() {
        ArrayList<Tag> ret = new ArrayList<>();
        for (Tag tag : this)
            ret.add(tag);
        return ret;
    }


    /**
     * Very simple implementation of XPath language interpreter.
     */
    public List<Tag> xPath(String path) {
        List<Tag> results = copyList();

        List<String> request = SU.charSplit(path, '/');

        for (int index = 0; index < request.size(); index++) {

            List<String> node = SU.charSplit(request.get(index), '&');

            String name = node.remove(0);
            for (int i = 0; i < results.size(); ) {
                if (!SU.fast_match(name, results.get(i).name)) {
                    results.remove(i);
                    continue;
                }
                i++;
            }

            for (String quiz : node) {
                List<String> property = SU.charSplit(quiz, 2, '=');

                String p_name = property.get(0);
                String p_val = property.get(1);

                for (int i = 0; i < results.size(); ) {
                    Tag proc = results.get(i);

                    if (!(proc.props.containsKey(p_name) && SU.fast_match(p_val, proc.get(p_name)))) {
                        results.remove(i);
                        continue;
                    }

                    i++;
                }

            }

            if (index < request.size() - 1) {
                ArrayList<Tag> top = new ArrayList<>();
                for (Tag tag : results)
                    if (tag.isOpening())
                        top.addAll(getTopChildren(tag));
                results = top;
            }

        }

        return results;
    }

    public String xPathStr(String str) {
        try {
            return getContents(xPathFirstTag(str));
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    public Tag xPathFirstTag(String str) {
        try {
            return xPath(str).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}