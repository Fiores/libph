package com.cab404.libph.tests;

import com.cab404.libph.pages.LetterPage;
import com.cab404.libph.pages.LetterTablePage;
import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.tests.Test;

/**
 * Если что - постучитесь ко мне на табун, добавлю в письмо и тестовый блог.
 *
 * @author cab404
 */
public class LetterTest extends Test {

    @Override
    public void test(AccessProfile profile) {
        LetterTablePage letters = new LetterTablePage(1);
        letters.fetch(profile);

        LetterPage page = new LetterPage(195);
        page.fetch(profile);

        assertEquals("Участники", page.header.recipients.toArray(), new String[]{"cab404", "Fiores"});
        assertEquals("Название", page.header.title, "Адрес для открытки");
//        assertEquals("Текст", page.header.text, "Это будет чистое и красивое тестовое письмо.");
//        assertEquals("Первый комментарий", page.comments.get(0).text, "— Upstream-ish stuff.");
    }

}