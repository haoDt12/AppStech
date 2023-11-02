package com.datn.shopsale.ui.cart;

import android.content.Context;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.PreferenceManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartPresenter {
    private final ICartView iCartView;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private String userId;

    public CartPresenter(ICartView iCartView) {
        this.iCartView = iCartView;
    }

    public void getDataCart(Context context) {
        // get from API


        // Fake data
        List<Cart> listCart = new ArrayList<>();
        Cart itemCart = new Cart();
        itemCart.setId("id1");
        itemCart.setImg("https://cdn.hoanghamobile.com/i/productlist/dsp/Uploads/2022/09/08/2222.png");
        Date date = new Date();
        itemCart.setDate(date);
        itemCart.setQuantity(2);
        itemCart.setPrice(26000.0 * itemCart.getQuantity());
        ArrayList<String> listProductId = new ArrayList<>();
        listProductId.add("IP14");
        listProductId.add("IP13");
        itemCart.setProductId(listProductId);
        String idUser = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(context, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);

        CallbackManager callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (currentUser != null) {
            idUser = itemCart.getUserId();
        } else if (acct != null) {
            idUser = acct.getId();
        } else if (accessToken != null && !accessToken.isExpired()) {
            idUser = accessToken.getUserId();
        }
        itemCart.setUserId(idUser);
        itemCart.setTitle("IPhone");
        itemCart.setStatus(1);
        itemCart.setTotal(52000000);
        itemCart.setStatus(Constants.STATUS_CART.DEFAULT.getValue());

        Cart itemCart2 = new Cart();
        itemCart2.setId("id1");
        itemCart2.setImg("https://cdn.hoanghamobile.com/i/productlist/dsp/Uploads/2022/09/08/2222.png");
        Date date2 = new Date();
        itemCart2.setDate(date2);
        itemCart2.setQuantity(1);
        itemCart2.setPrice(26000.0 * itemCart2.getQuantity());
        ArrayList<String> listProductId2 = new ArrayList<>();
        listProductId2.add("IP14");
        listProductId2.add("IP13");
        itemCart2.setProductId(listProductId2);
        itemCart2.setUserId(idUser);
        itemCart2.setTitle("IPhone");
        itemCart2.setStatus(1);
        itemCart2.setTotal(52000000);
        itemCart2.setStatus(Constants.STATUS_CART.DEFAULT.getValue());

        listCart.add(itemCart);
        listCart.add(itemCart2);

        iCartView.getDataCartSuccess(listCart);
    }

    public void updateStatus(int status, int pos) {
        // Update Status Cart Here

    }

    public void updateQuantity(int quantity, int pos) {
        // Update Quantity Cart Here
    }

    public void updatePrice(double price, int pos) {
        // Update Price Cart Here
    }
}
