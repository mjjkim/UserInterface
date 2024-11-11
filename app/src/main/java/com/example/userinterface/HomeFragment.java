package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class HomeFragment extends Fragment {
    // 리뷰 리사이클러뷰 변수
    RecyclerView bookRecyclerView;

    // 리뷰 리스트
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(getLayoutInflater());

        //잠깐 대기
//        bookRecyclerView = binding.review;
//
//        // MaterialButtonToggleGroup 및 버튼 초기화
//        MaterialButtonToggleGroup sortToggleGroup = binding.toggleButton;
//
//        // 최신순 버튼을 기본으로 선택
//        sortToggleGroup.check(R.id.button_sort_recent);
//
//        // 버튼 선택 이벤트 리스너 설정
//        sortToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
//            @Override
//            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
//                if (isChecked) {
//                    if (checkedId == R.id.button_sort_recent) {// 최신순 정렬 함수 호출
////                            sortByRecent();
//                    } else if (checkedId == R.id.button_sort_popular) {// 인기순 정렬 함수 호출
////                            sortByPopularity();
//                    }
//                }
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}