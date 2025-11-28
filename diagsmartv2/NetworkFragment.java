package com.example.diagsmartv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class NetworkFragment extends Fragment {

    TextView tvIPAddress, tvIPAddressII, tvWifiNum, tvIPv6Address,
            tvGateway, tvSubnetMask,  tvDNS1, tvDNS2, tvLeaseDuration, tvInterface,
            tvLinkSpeed, tvFrequency, tvWifiStand, tvSecurityType, tvWifiData, tvDeviceType;

    Context context;

    public NetworkFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        initReferences(view);
        context = requireContext();
        getConnectionStatus();
        getIPAddress();
        getIPv6Address();
        getGatewayIPAddress();
        getAndSetSubnetMask();
        getDNSAddresses();
        getLeaseDuration();
        getNetworkInterface();
        getLinkSpeed();
        getFrequency();
        getWifiStandard();
        getSecurityType();
        getDeviceType();
        getWifiNum();

        return view;
    }

    private void initReferences(View view) {
        tvIPAddress = view.findViewById(R.id.tvIpAddress);
        tvIPAddressII = view.findViewById(R.id.tvIPAddressII);
        tvWifiNum = view.findViewById(R.id.tvWifiNum);
        tvIPv6Address = view.findViewById(R.id.tvIPv6Address);
        tvGateway = view.findViewById(R.id.tvGateway);
        tvSubnetMask = view.findViewById(R.id.tvSubnetMask);
        tvDNS1 = view.findViewById(R.id.tvDNS1);
        tvDNS2 = view.findViewById(R.id.tvDNS2);
        tvLeaseDuration = view.findViewById(R.id.tvLeaseDuration);
        tvInterface = view.findViewById(R.id.tvInterface);
        tvLinkSpeed = view.findViewById(R.id.tvLinkSpeed);
        tvFrequency = view.findViewById(R.id.tvFrequency);
        tvWifiStand = view.findViewById(R.id.tvWifiStand);
        tvSecurityType = view.findViewById(R.id.tvSecurityType);
        tvWifiData = view.findViewById(R.id.tvWiFiData);
        tvDeviceType = view.findViewById(R.id.tvDeviceType);
    }

    @SuppressLint("SetTextI18n")
    private void getConnectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    tvWifiData.setText(context.getString(R.string.wifi));
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    tvWifiData.setText(context.getString(R.string.mobile_data));
                } else {
                    tvWifiData.setText(context.getString(R.string.other));
                }
                return;
            }
        }
        tvWifiData.setText(context.getString(R.string.no_connection));
    }

    @SuppressLint("DefaultLocale")
    private void getIPAddress() {
        String ipAddress = context.getString(R.string.no_ip_address);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();

        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            // Wi-Fi
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipInt = wifiInfo.getIpAddress();
                ipAddress = String.format("%d.%d.%d.%d",
                        (ipInt & 0xff), (ipInt >> 8 & 0xff),
                        (ipInt >> 16 & 0xff), (ipInt >> 24 & 0xff));
            }
            // Mobile Data
            else if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        if (!intf.isLoopback() && intf.isUp()) {
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    ipAddress = inetAddress.getHostAddress();
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("NetworkFragment", "Error getting IP address", e);
                }
            }
        }

        tvIPAddress.setText(ipAddress);
        tvIPAddressII.setText(ipAddress);
    }

    private void getIPv6Address() {
        String ipv6Address = context.getString(R.string.not_available);

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                    InetAddress address = linkAddress.getAddress();
                    if (address instanceof Inet6Address) {
                        // Include both global and link-local IPv6 addresses
                        ipv6Address = address.getHostAddress();
                        assert ipv6Address != null;
                        if (ipv6Address.contains("%")) {
                            ipv6Address = ipv6Address.split("%")[0];

                        }
                        break;
                    }
                }
            }
        }

        tvIPv6Address.setText(ipv6Address);
    }

    private void getGatewayIPAddress() {
        String gatewayIP = context.getString(R.string.not_available);

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                List<RouteInfo> routes = linkProperties.getRoutes();
                for (RouteInfo route : routes) {
                    if (route.isDefaultRoute()) {
                        InetAddress gateway = route.getGateway();
                        if (gateway != null) {
                            gatewayIP = gateway.getHostAddress();
                            break;
                        }
                    }
                }
            }
        }

        tvGateway.setText(gatewayIP);
    }

    @SuppressLint("DefaultLocale")
    private void getAndSetSubnetMask() {
        String subnetMask = context.getString(R.string.not_available);

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                List<LinkAddress> addresses = linkProperties.getLinkAddresses();
                for (LinkAddress address : addresses) {
                    if (address.getAddress() instanceof Inet4Address) {
                        int prefix = address.getPrefixLength();
                        int mask = 0xffffffff << (32 - prefix);
                        subnetMask = String.format("%d.%d.%d.%d",
                                (mask >> 24) & 0xff,
                                (mask >> 16) & 0xff,
                                (mask >> 8) & 0xff,
                                mask & 0xff);
                        break;
                    }
                }
            }
        }

        tvSubnetMask.setText(subnetMask);
    }

    private void getDNSAddresses() {
        String dns1 = context.getString(R.string.not_available);
        String dns2 = context.getString(R.string.not_available);

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                List<InetAddress> dnsServers = linkProperties.getDnsServers();
                if (!dnsServers.isEmpty()) {
                    dns1 = dnsServers.get(0).getHostAddress();
                    if (dnsServers.size() > 1) {
                        dns2 = dnsServers.get(1).getHostAddress();
                    }
                }
            }
        }

        tvDNS1.setText(dns1);
        tvDNS2.setText(dns2);
    }

    private void getLeaseDuration() {
        String leaseDuration = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                if (dhcpInfo != null) {
                    int leaseDurationSeconds = dhcpInfo.leaseDuration;
                    if (leaseDurationSeconds > 0) {
                        int hours = leaseDurationSeconds / 3600;
                        leaseDuration = hours + "H";
                    }
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                leaseDuration = context.getString(R.string.switch_to_wifi);
            }
        }

        tvLeaseDuration.setText(leaseDuration);
    }


    private void getNetworkInterface() {
        String interfaceName = context.getString(R.string.not_available);

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network activeNetwork = connectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
            if (linkProperties != null) {
                List<LinkAddress> inetAddresses = linkProperties.getLinkAddresses();
                for (LinkAddress inetAddress : inetAddresses) {
                    try {
                        // Get the NetworkInterface corresponding to the InetAddress
                        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress.getAddress());
                        if (networkInterface != null) {
                            interfaceName = networkInterface.getName(); // Get the interface name
                            break; // Exit loop after finding the first valid interface
                        }
                    } catch (SocketException e) {
                        Log.e("NetworkFragment", "Error getting network interface");
                    }
                }
            }
        }

        tvInterface.setText(interfaceName);
    }

    private void getLinkSpeed() {
        String linkSpeed = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int speed = 0;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    speed = wifiInfo.getRxLinkSpeedMbps(); // Accurate link speed
                } else {
                    speed = wifiInfo.getLinkSpeed(); // Deprecated but works for older versions
                }

                if (speed > 0) {
                    linkSpeed = speed + "Mbps";
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                linkSpeed = context.getString(R.string.switch_to_wifi);
            }
        }

        tvLinkSpeed.setText(linkSpeed);
    }


    private void getFrequency() {
        String frequency = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int freqMhz = wifiInfo.getFrequency();
                frequency = freqMhz + "MHz";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                frequency = context.getString(R.string.switch_to_wifi);
            }
        }

        tvFrequency.setText(frequency);
    }

    private void getWifiStandard() {
        String wifiStandard = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    int wifiStandardInt = wifiManager.getConnectionInfo().getWifiStandard();
                    switch (wifiStandardInt) {
                        case ScanResult.WIFI_STANDARD_LEGACY:
                            wifiStandard = "Wi-Fi 802.11a/b/g";
                            break;
                        case ScanResult.WIFI_STANDARD_11N:
                            wifiStandard = "Wi-Fi 802.11n (Wi-Fi 4)";
                            break;
                        case ScanResult.WIFI_STANDARD_11AC:
                            wifiStandard = "Wi-Fi 802.11ac (Wi-Fi 5)";
                            break;
                        case ScanResult.WIFI_STANDARD_11AX:
                            wifiStandard = "Wi-Fi 802.11ax (Wi-Fi 6)";
                            break;
                        case ScanResult.WIFI_STANDARD_11AD:
                            wifiStandard = "Wi-Fi 802.11ad";
                            break;
                        case ScanResult.WIFI_STANDARD_11BE:
                            wifiStandard = "Wi-Fi 802.11be (Wi-Fi 7)";
                            break;
                        case ScanResult.WIFI_STANDARD_UNKNOWN:
                            wifiStandard = context.getString(R.string.unknown_wifi_standard);
                            break;
                        default:
                            wifiStandard = context.getString(R.string.unrecognized_wifi_standard);
                            break;
                    }
                } else {
                    wifiStandard = context.getString(R.string.wifi_standard_not_available);
                }

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                wifiStandard = context.getString(R.string.switch_to_wifi);
            }
        }

        tvWifiStand.setText(wifiStandard);
    }

    private void getSecurityType() {
        String securityType = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                int securityTypeInt = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    securityTypeInt = wifiInfo.getCurrentSecurityType();
                }
                switch (securityTypeInt) {
                    case WifiInfo.SECURITY_TYPE_OPEN:
                        securityType = context.getString(R.string.open);
                        break;
                    case WifiInfo.SECURITY_TYPE_WEP:
                        securityType = "WEP";
                        break;
                    case WifiInfo.SECURITY_TYPE_PSK:
                        securityType = "PSK";
                        break;
                    case WifiInfo.SECURITY_TYPE_EAP:
                        securityType = "EAP";
                        break;
                    case WifiInfo.SECURITY_TYPE_SAE:
                        securityType = "SAE";
                        break;
                    case WifiInfo.SECURITY_TYPE_OWE:
                        securityType = "OWE";
                        break;
                    default:
                        securityType = context.getString(R.string.unknown);
                        break;
                }
            } else {
                securityType = context.getString(R.string.not_available);
            }
        } else if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            securityType = context.getString(R.string.switch_to_wifi);
        }

        tvSecurityType.setText(securityType);
    }

    private void getDeviceType() {
        String deviceType = context.getString(R.string.not_available);
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager telephonyManager = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
                int phoneType = telephonyManager.getPhoneType();

                switch (phoneType) {
                    case TelephonyManager.PHONE_TYPE_GSM:
                        deviceType = "GSM";
                        break;
                    case TelephonyManager.PHONE_TYPE_CDMA:
                        deviceType = "CDMA";
                        break;
                    default:
                        deviceType = context.getString(R.string.unknown);
                        break;
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                deviceType = context.getString(R.string.switch_mobile_data);
            }
        }

        tvDeviceType.setText(deviceType);
    }

    private void getWifiNum() {
        String wifiNum = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int frequency = wifiInfo.getFrequency();
            String band = (frequency > 5000) ? "5 GHz" : "2.4 GHz";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                int wifiStandard = wifiInfo.getWifiStandard();
                switch (wifiStandard) {
                    case ScanResult.WIFI_STANDARD_11AC:
                        wifiNum = "Wi-Fi 5 (" + band + ")";
                        break;
                    case ScanResult.WIFI_STANDARD_11AX:
                        wifiNum = "Wi-Fi 6 (" + band + ")";
                        break;
                    case ScanResult.WIFI_STANDARD_11N:
                        wifiNum = "Wi-Fi 4 (" + band + ")";
                        break;
                    default:
                        wifiNum = "Wi-Fi (" + band + ")";
                        break;
                }
            } else {
                wifiNum = "Wi-Fi (" + band + ")";
            }
        }

        tvWifiNum.setText(wifiNum);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
                ((MainActivity) requireActivity()).highlightDashboard();
            }
        });
    }

}