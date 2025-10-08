package com.example.todo6.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.todo6.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var auth : FirebaseAuth
private lateinit var navControl : NavController
class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        navControl =view.findNavController()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)
            if(auth.currentUser != null){
                navControl.navigate(R.id.action_splashFragment_to_homeFragment)
            }else{
                navControl.navigate(R.id.action_splashFragment_to_signInFragment)
            }
        }
    }

}