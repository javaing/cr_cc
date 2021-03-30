package com.aliee.quei.mo.database;


import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by liyang on 2018/5/15 0015.
 */

public class DBMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema.get("ComicBookBean")
                    .addField("thumb_x",String.class,FieldAttribute.REQUIRED);
            schema.create("SearchHistoryBean")
                    .addField("time",Long.class,FieldAttribute.REQUIRED)
                    .addField("keyword",String.class,FieldAttribute.PRIMARY_KEY,FieldAttribute.REQUIRED);
            oldVersion++;
        }
        if (oldVersion == 1) {
            schema.get("ComicBookBean")
                    .renameField("thumb_x","thumbX");
            oldVersion ++;
        }
        if (oldVersion == 2) {
            schema.get("CatalogItemBean")
                    .renameField("book_bean","bookBean");
            oldVersion++;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
