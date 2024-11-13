package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // View Binding 객체

    private RecyclerView recyclerView; //리사이클러 뷰
    private ItemAdapter itemAdapter; //어댑터
    private List<BookItem> itemList; // bookitem 리스트

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 아이템 리스트 예시
        recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        itemList.add(new BookItem("1aaaa", "2", "33", "4"));
        itemList.add(new BookItem("1sf", "2", "33", "4"));
        itemList.add(new BookItem("1fas", "2", "33", "4"));
        itemList.add(new BookItem("1asfas", "2", "33", "4"));
        itemList.add(new BookItem("1da", "2", "33", "4"));
        itemList.add(new BookItem("1aaaa", "2", "33", "4"));
        itemList.add(new BookItem("1sf", "2", "33", "4"));
        itemList.add(new BookItem("1fas", "2", "33", "4"));
        itemList.add(new BookItem("1asfas", "2", "33", "4"));
        itemList.add(new BookItem("1da", "2", "33", "4"));

        // 어댑터 연결
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 토글버튼 클릭시 최신순, 인기순 정렬
        MaterialButtonToggleGroup toggleGroup = binding.toggleButtonGroup;
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.mbt_record) {
                    // 최신순 정렬

                } else if (checkedId == R.id.mbt_text) {
                    // 인기순 정렬

                }
            }
        });
    }
    public void onDestroyView() {
        super.onDestroyView();
        // 뷰 참조를 해제하여 메모리 누수 방지
        recyclerView = null;
        binding = null;
    }
}