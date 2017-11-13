package ga.chrom_web.player.multiplayer.di;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import ga.chrom_web.player.multiplayer.BuildConfig;
import io.fabric.sdk.android.Fabric;


public class App extends Application {

    public static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.BUILD_TYPE.equals("debug")) {
            Fabric.with(this, new Crashlytics());
        }
        component = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
