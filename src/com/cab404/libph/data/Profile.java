package com.cab404.libph.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cab404
 */
public class Profile {

    public String name, login, about, small_icon, mid_icon, big_icon, photo;

    public Integer id;

    public final ArrayList<Profile> partial_friend_list;

    public final List<KV<String, String>> personal;

    public final List<KV<String, String>> contacts;

    public boolean is_system = false;

    public Profile() {
        personal = new ArrayList<>();
        contacts = new ArrayList<>();
        partial_friend_list = new ArrayList<>();
    }

    public List<String> getContact(String type) {
        ArrayList<String> list = new ArrayList<String>();

        for (KV<String, String> e : contacts)
            if (e.k.equals(type))
                list.add(e.v);

        return list;
    }

    public String getData(String type) {
        for (KV<String, String> e : personal)
            if (e.k.equals(type))
                return e.v;

        return null;
    }


    public void fillImages() {
        String uni = "";
        if (small_icon != null && !small_icon.isEmpty()) uni = small_icon.replace("24x24", "***");
        else if (mid_icon != null && !mid_icon.isEmpty()) uni = mid_icon.replace("48x48", "***");
        else if (big_icon != null && !big_icon.isEmpty()) uni = big_icon.replace("100x100", "***");

        small_icon = uni.replace("***", "24x24");
        mid_icon = uni.replace("***", "48x48");
        big_icon = uni.replace("***", "100x100");
    }

    public static enum ContactType {
        PHONE("phone", "Телефон"),
        EMAIL("mail", "Электропочта"),
        SKYPE("skype", "Skype"),
        ICQ("icq", "ICQ"),
        SITE("www", "Сайт"),
        TWITTER("twitter", "Твиттер"),
        XMPP("XMPP", "XMPP"),
        FACEBOOK("facebook", "Facebook"),
        VKONTAKTE("vkontakte", "ВК"),
        DISCORD("discord", "Дискорд"),
        STEAM("steam", "Steam"),
        BATTLE("battle", "Battle.net"),
        ODNOKLASSNIKI("odnoklassniki", "Одноклассники"),;

        public String name;
        public String normal_name;

        ContactType(String name, String normal_name) {
            this.name = name;
            this.normal_name = normal_name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static enum UserInfoType {
        GENDER("Пол"),
        BIRTHDAY("Дата рождения"),
        PLACE("Местоположение"),
        CREATED("Создал"),
        MODERATOR("Модерирует"),
        HAS_INVITED("Приглашенные"),
        INVITED_BY("Пригласил"),
        ADMIN("Администрирует"),
        BELONGS("Состоит в"),
        REG_DATE("Зарегистрирован"),
        PUBLICATIONS("Публикации"),
        LAST_VISITED("Последний визит"),;

        public String name;

        UserInfoType(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Возвращает тип данных по его нормальному названию на русском языке, перебирая все элементы.
     * Если не находит, то кидает RuntimeException.
     * <p/>
     * Для этого места нормально.
     * Вряд ли понадобится неимоверно часто доставать типы контактов,
     * да и самих типов мало.
     * Иначе я бы добавил бинарный поиск, тем более, что массив данных
     * не меняется.
     * Тоже относится и к {@link com.cab404.libph.data.Profile#getContactType(String)}.
     *
     * @throws java.lang.RuntimeException Если не находит тип данных.
     */
    public static UserInfoType getDataType(String name) {
        for (UserInfoType type : UserInfoType.values())
            if (type.name.equals(name))
                return type;
        throw new RuntimeException("Неподдерживаемый тип данных: " + name);
    }

    /**
     * @see com.cab404.libph.data.Profile#getDataType(String)
     */
    public static ContactType getContactType(String name) {
        for (ContactType type : ContactType.values())
            if (type.name.equals(name))
                return type;
        throw new RuntimeException("Неподдерживаемый тип данных: " + name);
    }


}
