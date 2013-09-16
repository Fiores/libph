package com.cab404.libtabun.parts;

import com.cab404.libtabun.U;
import com.cab404.libtabun.facility.HTMLParser;
import com.cab404.libtabun.facility.ResponseFactory;
import javolution.util.FastList;

/**
 * <strong>Page With Post Labels</strong>
 * <p/>
 * Проще говоря, страничка с несколькими постами на ней, будь то лента или блог.
 *
 * @author cab404
 */
public class PaWPoL extends Part {
    FastList<Post> posts;

    public static class PostLabel extends Part {
        public String name, votes, author;
        public String content, time;
        public String[] tags;
        public Blog blog;
        public int comments = 0, comments_new = 0;


        public PostLabel() {
            type = "Topic";
            name = votes = author = time = content = "";
        }
    }

    public static class PostLabelParser implements ResponseFactory.Parser {
        boolean writing = false;
        String text = "";
        public PostLabel pl = new PostLabel();

        @Override
        public boolean line(String line) {
            if (!writing) if (line.trim().equals("<article class=\"topic topic-type-topic js-topic\">")) writing = true;
            else ;
            else if (line.trim().equals("</article> <!-- /.topic -->")) {
                text += line + "\n";

                HTMLParser raw = new HTMLParser(text);

                pl.id = U.parseInt(U.sub(raw.getTagByProperty("class", "vote-item vote-up").props.get("onclick"), "(", ","));
                pl.content = raw.getContents(raw.getTagByProperty("class", "topic-content text")).replace("\t", "").trim();
                pl.name = U.removeAllTags(raw.getContents(raw.getTagByProperty("class", "topic-title word-wrap"))).trim();

                int blog_tag;
                try {
                    blog_tag = raw.getTagIndexByProperty("class", "topic-blog");
                } catch (Error e) {
                    blog_tag = raw.getTagIndexByProperty("class", "topic-blog private-blog");
                }
                pl.blog = new Blog();
                pl.blog.name = raw.getContents(blog_tag);
                pl.blog.url_name = U.bsub(raw.tags.get(blog_tag).props.get("href"), "/blog/", "/");

                int time_tag = raw.getTagIndexForName("time");
                pl.time = raw.getContents(time_tag).trim();
                pl.date = U.convertDatetime(raw.tags.get(time_tag).props.get("datetime"));
                pl.votes = raw.getContents(raw.getTagIndexByProperty("id", "vote_total_topic_" + pl.id)).trim();

                FastList<HTMLParser.Tag> raw_tags = raw.getAllTagsByProperty("rel", "tag");
                pl.tags = new String[raw_tags.size()];
                for (int i = 0; i != raw_tags.size(); i++) {
                    pl.tags[i] = raw.getContents(raw_tags.get(i));
                }

                String comments = U.removeAllTags(raw.getContents(raw.getTagIndexByProperty("class", "topic-info-comments"))).trim();
                pl.comments = U.parseInt(comments.split("\\Q+\\E")[0]);
                try {
                    pl.comments_new = U.parseInt(comments.split("\\Q+\\E")[1]);
                } catch (Exception ex) {
                    pl.comments_new = 0;
                }
                U.v(pl.comments_new);
                return false;
            }
            if (writing) text += line + "\n";
            return true;
        }
    }

    public static class PostLabelListParser implements ResponseFactory.Parser {
        public PostLabelListParser(EndsWith endsWith) {
            plp = new PostLabelParser();
            labels = new FastList<>();
            this.endsWith = endsWith;
        }

        public enum EndsWith {
            /**
             * Для блогов
             */
            PAGINATOR("<div class=\"pagination\">"),
            /**
             * Для главной страницы
             */
            FEED_LOADER("<div id=\"userfeed_loaded_topics\"></div>"),;

            String val;

            private EndsWith(String value) {
                val = value;
            }
        }

        private final EndsWith endsWith;
        private PostLabelParser plp;
        public FastList<PostLabel> labels;

        @Override
        public boolean line(String line) {
            if (!line.trim().equals(endsWith.val)) {
                if (!plp.line(line)) {
                    labels.add(plp.pl);
                    plp = new PostLabelParser();
                }
                return true;
            } else return false;
        }
    }

}
