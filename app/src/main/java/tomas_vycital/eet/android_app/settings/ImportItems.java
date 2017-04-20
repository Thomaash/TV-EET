package tomas_vycital.eet.android_app.settings;

import android.os.Handler;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.error.UnsupportedImportItemsVersion;

class ImportItems implements Runnable {
    private final Handler handler;
    private final String url;

    ImportItems(String url, Handler handler) {
        this.handler = handler;
        this.url = url;

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            int responseCode = con.getResponseCode();
            this.handler.sendMessage(Messages.generateMessage(new Exception("Response Code : " + responseCode)));

            Settings.importItems((new Scanner(con.getInputStream())).useDelimiter("\\A").next());

            this.handler.sendEmptyMessage(Messages.itemsChanged.ordinal());
            this.handler.sendMessage(Messages.generateMessage("Zboží bylo úspěšně importováno"));
        } catch (ProtocolException e) {
            this.handler.sendMessage(Messages.generateMessage("Chyba komunikace"));
        } catch (MalformedURLException e) {
            this.handler.sendMessage(Messages.generateMessage("Byla zadána neplatná adresa"));
        } catch (IOException e) {
            this.handler.sendMessage(Messages.generateMessage("Chyba komunikace"));
        } catch (JSONException e) {
            this.handler.sendMessage(Messages.generateMessage("Byla přijata nečitelná data"));
        } catch (UnsupportedImportItemsVersion e) {
            this.handler.sendMessage(Messages.generateMessage("Stažená data mají nepodporovanou verzi: " + e.getVersion()));
        }
    }
}
