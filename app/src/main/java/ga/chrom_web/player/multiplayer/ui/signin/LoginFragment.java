package ga.chrom_web.player.multiplayer.ui.signin;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.databinding.FragmentLoginBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private FragmentLoginBinding mBinding;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mBinding.setColor(Color.BLACK);

        mBinding.colorPicker.setOnClickListener(view -> {
            ColorPicker colorPicker = new ColorPicker(getActivity());
            colorPicker.setCallback(color -> {

                mViewModel.setColor(color);
                colorPicker.dismiss();
            });
            colorPicker.show();
        });
        // TODO: make it as binding
        mBinding.btnLogin.setOnClickListener(view -> {
            mViewModel.login(mBinding.etNick.getText().toString());
            ((MainActivity) getActivity()).startPlayer();
        });


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mBinding.setViewModel(mViewModel);

        mViewModel.getColor().observe(this, color -> {
            mBinding.setColor(color);
        });
    }
}
