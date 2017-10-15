package ga.chrom_web.player.multiplayer.di;

import javax.inject.Singleton;

import dagger.Component;
import ga.chrom_web.player.multiplayer.ChatSocketManager;
import ga.chrom_web.player.multiplayer.ConnectionSocketManager;
import ga.chrom_web.player.multiplayer.PlayerSocketManager;
import ga.chrom_web.player.multiplayer.SocketManager;
import ga.chrom_web.player.multiplayer.SmilesLoader;
import ga.chrom_web.player.multiplayer.ui.player.PlayerViewModel;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import ga.chrom_web.player.multiplayer.ui.signin.LoginViewModel;

@Singleton
@Component(modules = {AppModule.class, AndroidModule.class})
public interface AppComponent {
    void inject(PlayerSocketManager playerManager);

    void inject(ConnectionSocketManager connectionManager);

    void inject(ChatSocketManager chatManager);

    void inject(SocketManager socketManager);

    void inject(PlayerViewModel playerViewModel);

    void inject(SharedPreferenceHelper sharedPreferenceHelper);

    void inject(LoginViewModel loginViewModel);

    void inject(SmilesLoader smilesLoader);
}
