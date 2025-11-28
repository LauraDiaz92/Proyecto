package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diagsmartv2.DashboardFragment;
import com.example.diagsmartv2.MainActivity;
import com.example.diagsmartv2.R;

public class TestsFragment extends Fragment implements TestsInteractiveAdapter.OnTestSelectedListener {

    private ConstraintLayout constAutomaticTests;
    private RecyclerView rvTestInteractiveList;
    private TestsInteractiveAdapter adapter;

    public TestsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        initReferences(view);
        configureRecyclerView();
        constAutomaticTests.setOnClickListener(v -> openAutomaticTests());

        return view;
    }

    private void initReferences(View view) {
        constAutomaticTests = view.findViewById(R.id.constAutomaticTests);
        rvTestInteractiveList = view.findViewById(R.id.rvTestsInteractiveList);
    }

    private void configureRecyclerView() {
        adapter = new TestsInteractiveAdapter(requireContext(), this);
        rvTestInteractiveList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTestInteractiveList.setAdapter(adapter);
    }

    // Manejo del evento de selección de test interactivo desde el adaptador
    @Override
    public void onTestSelected(int position, String testName) {
        Fragment fragment = null;
        switch (position) {
            case 0: fragment = new TestDisplayMainFragment(); break;
            case 1: fragment = new TestMultitouchFragment(); break;
            case 2: fragment = new TestFlashlightFragment(); break;
            case 3: fragment = new TestLoudspeakerFragment(); break;
            case 4: fragment = new TestEarSpeakerFragment(); break;
            case 5: fragment = new TestMicrophoneFragment(); break;
            case 6: fragment = new TestEarProximityFragment(); break;
            case 7: fragment = new TestLightSensorFragment(); break;
            case 8: fragment = new TestChargingFragment(); break;
            case 9: fragment = new TestBluetoothFragment(); break;
            case 10: fragment = new TestVolumeUpButtonFragment(); break;
            case 11: fragment = new TestVolumeDownButtonFragment(); break;
        }
        if (fragment != null) {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void openAutomaticTests() {
        Fragment fragment = new TestAutomaticFragment();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
