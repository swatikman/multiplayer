package ga.chrom_web.player.multiplayer.di;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Component;
import ga.chrom_web.player.multiplayer.ChatSocketManager;
import ga.chrom_web.player.multiplayer.ConnectionSocketManager;
import ga.chrom_web.player.multiplayer.PlayerSocketManager;
import ga.chrom_web.player.multiplayer.SocketManager;
import ga.chrom_web.player.multiplayer.SmilesLoader;
import ga.chrom_web.player.multiplayer.api.ApiManager;
import ga.chrom_web.player.multiplayer.ui.SmilesAdapter;
import ga.chrom_web.player.multiplayer.ui.player.ChatAdapter;
import ga.chrom_web.player.multiplayer.ui.player.PlayerActivityViewModel;
import ga.chrom_web.player.multiplayer.ui.player.PlayerViewModel;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import ga.chrom_web.player.multiplayer.ui.rooms.RoomsFragmentViewModel;
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

    void inject(@NotNull SmilesAdapter smilesAdapter);

    void inject(ChatAdapter chatAdapter);

    void inject(@NotNull PlayerActivityViewModel playerActivityViewModel);

    void inject(@NotNull ApiManager apiManager);

    void inject(@NotNull RoomsFragmentViewModel roomsFragmentViewModel);
}
