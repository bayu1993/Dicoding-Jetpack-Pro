package com.bayuspace.academy.ui.academy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayuspace.academy.databinding.FragmentAcademyBinding
import com.bayuspace.academy.ui.detail.DetailActivity
import com.bayuspace.academy.viewmodel.ViewModelFactory

class AcademyFragment : Fragment() {
    private lateinit var academyAdapter: AcademyAdapter
    private lateinit var _binding: FragmentAcademyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAcademyBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireActivity())
        )[AcademyViewModel::class.java]
        academyAdapter = AcademyAdapter { course ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_COURSE, course.courseId)
            requireContext().startActivity(intent)
        }
        _binding.progressBar.visibility = View.VISIBLE
        viewModel.getCourses().observe(requireActivity(), {
            _binding.progressBar.visibility = View.GONE
            academyAdapter.setCourse(it)
        })
        if (activity != null) {
            with(_binding.rvAcademy) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = academyAdapter
            }
        }
    }
}