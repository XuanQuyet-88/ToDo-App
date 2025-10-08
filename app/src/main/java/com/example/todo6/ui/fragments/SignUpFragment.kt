package com.example.todo6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.todo6.R
import com.example.todo6.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
private lateinit var navController: NavController
private lateinit var binding: FragmentSignUpBinding

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        navController = view.findNavController()
    }

    private fun registerEvents() {
        binding.tvAuth.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.btnNext.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()
            val repass = binding.edtRePassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()) {
                if(pass == repass && pass.length >= 6){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
                            binding.progressCircular.visibility = View.GONE
                        } else {
                            binding.progressCircular.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Your email or password is incorrect. Try again!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else if (pass.length < 6) {
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(pass != repass) {
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else if(email.isEmpty()){
                binding.progressCircular.visibility = View.GONE
                binding.edtEmail.error = "Email is required"
                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            }else if(pass.isEmpty()){
                binding.progressCircular.visibility = View.GONE
                binding.edtPassword.error = "Password is required"
                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            }else if(repass.isEmpty()){
                binding.progressCircular.visibility = View.GONE
                binding.edtRePassword.error = "Re-Password is required"
                Toast.makeText(context, "Re-Password is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}