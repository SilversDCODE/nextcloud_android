package com.owncloud.gshare.ui

import android.annotation.SuppressLint
import android.net.http.SslCertificate
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentManager
import com.owncloud.gshare.authentication.AuthenticatorActivity
import com.owncloud.android.lib.common.network.NetworkUtils
import com.owncloud.android.lib.common.utils.Log_OC
import com.owncloud.gshare.ui.dialog.SslUntrustedCertDialog
import java.io.ByteArrayInputStream
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

open class NextcloudWebViewClient(val supportFragmentManager: FragmentManager) : WebViewClient() {

    private val tag: String? = NextcloudWebViewClient::class.simpleName

    @Suppress("TooGenericExceptionCaught")
    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        val cert = error?.let { getX509CertificateFromError(it) }
        try {
            if (NetworkUtils.isCertInKnownServersStore(cert, view?.context?.applicationContext)) {
                handler.proceed()
            } else {
                showUntrustedCertDialog(cert, error, handler)
            }
        } catch (e: Exception) {
            Log_OC.e(tag, "Cert could not be verified")
        }
    }

    /**
     * Obtain the X509Certificate from SslError
     *
     * @param error SslError
     * @return X509Certificate from error
     */
    open fun getX509CertificateFromError(error: SslError): X509Certificate? {
        val bundle = SslCertificate.saveState(error.certificate)
        val x509Certificate: X509Certificate?
        val bytes = bundle.getByteArray("x509-certificate")
        x509Certificate = if (bytes == null) {
            null
        } else {
            try {
                val certFactory = CertificateFactory.getInstance("X.509")
                val cert = certFactory.generateCertificate(ByteArrayInputStream(bytes))
                cert as X509Certificate
            } catch (e: CertificateException) {
                null
            }
        }
        return x509Certificate
    }

    /**
     * Show untrusted cert dialog
     */
    private fun showUntrustedCertDialog(
        x509Certificate: X509Certificate?,
        error: SslError?,
        handler: SslErrorHandler?
    ) {
        // Show a dialog with the certificate info
        val dialog: _root_ide_package_.com.owncloud.gshare.ui.dialog.SslUntrustedCertDialog = if (x509Certificate == null) {
            _root_ide_package_.com.owncloud.gshare.ui.dialog.SslUntrustedCertDialog.newInstanceForEmptySslError(error, handler)
        } else {
            _root_ide_package_.com.owncloud.gshare.ui.dialog.SslUntrustedCertDialog.newInstanceForFullSslError(x509Certificate, error, handler)
        }
        val fm: FragmentManager = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.addToBackStack(null)
        dialog.show(ft, _root_ide_package_.com.owncloud.gshare.authentication.AuthenticatorActivity.UNTRUSTED_CERT_DIALOG_TAG)
    }
}
