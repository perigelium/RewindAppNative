package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.R;

public class FragClientAltuofiancoDetails extends Fragment implements View.OnClickListener
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    ClientInfoItem clientInfoItem;
    private boolean isPasswordVisible;
    private ImageButton ibShowPassword;
    private TextView tvWindecarePassword;
    private LinearLayout llShowPassword;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        if (getArguments() != null)
        {
            clientInfoItem = getArguments().getParcelable("clientInfoItem");
        }

        isPasswordVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_client_altuofianco_details, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        TextView tvDateInserted = (TextView) rootView.findViewById(R.id.tvDateInserted);
        tvDateInserted.setText(clientInfoItem.getData_inserimento());

        TextView tvCanon = (TextView) rootView.findViewById(R.id.tvCanon);
        tvCanon.setText(clientInfoItem.getTipo_contratto());

        TextView tvClientCode = (TextView) rootView.findViewById(R.id.tvClientCode);
        tvClientCode.setText(clientInfoItem.getCodice_cliente());

        TextView tvEmailShop = (TextView) rootView.findViewById(R.id.tvEmailShop);
        tvEmailShop.setText(clientInfoItem.getEmail_shop());

        TextView tvPointsAvailable = (TextView) rootView.findViewById(R.id.tvPointsAvailable);
        tvPointsAvailable.setText(String.valueOf(clientInfoItem.getPunti_disponibili()));

        TextView tvRIDstatus = (TextView) rootView.findViewById(R.id.tvRIDstatus);
        tvRIDstatus.setText(clientInfoItem.getStato_rid());

        TextView tvWindecareUsername = (TextView) rootView.findViewById(R.id.tvWindecareUsername);
        tvWindecareUsername.setText(clientInfoItem.getWindecare_user());

        tvWindecarePassword = (TextView) rootView.findViewById(R.id.tvWindecarePassword);
        tvWindecarePassword.setText(clientInfoItem.getWindecare_password());

        llShowPassword = (LinearLayout) rootView.findViewById(R.id.llShowPassword);
        ibShowPassword = (ImageButton) rootView.findViewById(R.id.ibShowPassword);
        llShowPassword.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentsBackStack();
        }

        if (view.getId() == R.id.llShowPassword)
        {
            showOrHidePassword();
        }
    }

    private void showOrHidePassword()
    {
        if (isPasswordVisible)
        {
            ibShowPassword.setBackgroundResource(R.drawable.eye);
            tvWindecarePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else
        {
            ibShowPassword.setBackgroundResource(R.drawable.eyedisable);
            tvWindecarePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        isPasswordVisible = !isPasswordVisible;
    }
}
