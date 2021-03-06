package com.cab404.libph.tests;

import com.cab404.libph.pages.BlogListPage;
import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.tests.Test;

/**
 * @author cab404
 */
public class BlogListTest extends Test {


    @Override
    public void test(AccessProfile profile) {
        BlogListPage page = new BlogListPage();
        page.fetch(profile);

        assertEquals("url-имя блога", page.blogs.get(0).url_name, "Ponyhawks");
        assertEquals("id блога", page.blogs.get(0).id, 5);

    }

}
