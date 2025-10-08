package com.example.todo6.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.todo6.R
import com.example.todo6.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth : FirebaseAuth
private lateinit var navController: NavController
private lateinit var binding: FragmentSignInBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.tvAuth.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnNext.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_signInFragment_to_homeFragment)
                        binding.progressCircular.visibility = View.GONE
                    }else{
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(context, "Your email or password is incorrect. Try again!!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(email.isEmpty()){
                binding.progressCircular.visibility = View.GONE
                binding.edtEmail.error = "Email is required"
                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                binding.progressCircular.visibility = View.GONE
                binding.edtPassword.error = "Password is required"
                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view: View) {
        navController = view.findNavController()
        auth = FirebaseAuth.getInstance()
    }
}