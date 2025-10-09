package com.example.todo6.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.todo6.R
import com.example.todo6.databinding.FragmentSignInBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun registerEvents() {
        binding.tvAuth.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnNext.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_signInFragment_to_homeFragment)
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
            } else if (email.isEmpty()) {
                binding.progressCircular.visibility = View.GONE
                binding.edtEmail.error = "Email is required"
                Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                binding.progressCircular.visibility = View.GONE
                binding.edtPassword.error = "Password is required"
                Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignInGoogle.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                signInWithGoogle()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
                binding.progressCircular.visibility = View.GONE
            }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend fun signInWithGoogle() {
        binding.progressCircular.visibility = View.VISIBLE

        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)  // Luôn hiển thị account picker
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(requireActivity(), request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                firebaseAuthWithGoogle(credential.idToken)
            } else {
                Toast.makeText(context, "Unrecognized credential type", Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility = View.GONE
            }
        } catch (e: GetCredentialCancellationException) {
            // Người dùng chủ động hủy (bấm ra ngoài hoặc back)
            Log.d("SignInFragment", "User cancelled the sign-in flow.")
            binding.progressCircular.visibility = View.GONE
            // Không cần hiển thị Toast vì đây là hành động chủ động của user
        } catch (e: NoCredentialException) {
            // Không tìm thấy tài khoản Google trên thiết bị
            Log.e("SignInFragment", "No credential found.", e)
            binding.progressCircular.visibility = View.GONE

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Không tìm thấy tài khoản Google")
                .setMessage("Để tiếp tục, bạn cần đăng nhập vào một tài khoản Google trên thiết bị này. Bạn có muốn thêm tài khoản ngay bây giờ không?")
                .setPositiveButton("Thêm tài khoản") { dialog, _ ->
                    val intent = Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                    intent.putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: GetCredentialException) {
            // Các lỗi khác liên quan đến credential
            Log.e("SignInFragment", "GetCredentialException: ", e)
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(context, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Catch-all cho các exception không mong đợi
            Log.e("SignInFragment", "Unexpected error during sign-in: ", e)
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(view: View) {
        navController = view.findNavController()
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(requireContext())
    }
}