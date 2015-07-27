package org.cryse.lkong.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

public class SyncUtils {
    public static final String SYNC_AUTHORITY = "org.cryse.lkong.data.provider";
    public static final int SYNC_FREQUENCE = 1800;

    public static void manualSync(Account account, String authority) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(account, authority, settingsBundle);
    }

    public static void setPeriodicSync(Account account, String authority, boolean removePreviousPeriodic, int interval) {
        if(removePreviousPeriodic)
            ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
        ContentResolver.addPeriodicSync(
                account,
                authority,
                Bundle.EMPTY,
                interval);
    }
}
