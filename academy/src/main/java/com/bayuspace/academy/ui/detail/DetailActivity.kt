package com.bayuspace.academy.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayuspace.academy.R
import com.bayuspace.academy.data.source.local.entity.CourseEntity
import com.bayuspace.academy.databinding.ActivityDetailBinding
import com.bayuspace.academy.databinding.ContainDetailCourseBinding
import com.bayuspace.academy.ui.reader.CourseReaderActivity
import com.bayuspace.academy.viewmodel.ViewModelFactory
import com.bayuspace.academy.vo.Status
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class DetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityDetailBinding
    private lateinit var _containBinding: ContainDetailCourseBinding
    private lateinit var detailAdapter: DetailCourseAdapter
    private lateinit var viewModel: DetailCourseViewModel
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        _containBinding = _binding.detailContain
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)


        supportActionBar?.setDisplayShowHomeEnabled(true)
        detailAdapter = DetailCourseAdapter()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailCourseViewModel::class.java]

        val extras = intent.extras
        if (extras != null) {
            val courseId = extras.getString(EXTRA_COURSE)
            if (courseId != null) {
                _containBinding.progressBar.visibility = View.VISIBLE
                viewModel.apply {
                    selectedCourse(courseId)
                    courseModule.observe(this@DetailActivity, {
                        if (it != null) {
                            when (it.status) {
                                Status.LOADING -> _containBinding.progressBar.visibility =
                                    View.VISIBLE
                                Status.SUCCESS -> {
                                    _containBinding.progressBar.visibility = View.GONE

                                    it.data?.mModules?.let { modules ->
                                        detailAdapter.setModule(
                                            modules
                                        )
                                    }
                                    it.data?.mCourse?.let { course -> populateCourse(course) }
                                }
                                Status.ERROR -> {
                                    _containBinding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@DetailActivity,
                                        "Terjadi kesalahan",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    })
                }
            }
        }

        with(_containBinding.rvModule) {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = detailAdapter
            val itemDecoration =
                DividerItemDecoration(this@DetailActivity, DividerItemDecoration.HORIZONTAL)
            addItemDecoration(itemDecoration)
        }

    }

    private fun populateCourse(courseEntity: CourseEntity) {
        _containBinding.textTitle.text = courseEntity.title
        _containBinding.textDescription.text = courseEntity.description
        _containBinding.textDate.text =
            resources.getString(R.string.deadline_date, courseEntity.deadline)

        Glide.with(this)
            .load(courseEntity.imagePath)
            .transform(RoundedCorners(20))
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
            )
            .into(_containBinding.imagePoster)

        _containBinding.btnStart.setOnClickListener {
            val intent = Intent(this, CourseReaderActivity::class.java)
            intent.putExtra(CourseReaderActivity.EXTRA_COURSE_ID, courseEntity.courseId)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        this.menu = menu
        viewModel.courseModule.observe(this) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> _containBinding.progressBar.visibility = View.VISIBLE
                    Status.SUCCESS -> if (it.data != null) {
                        _containBinding.progressBar.visibility = View.GONE
                        val state = it.data.mCourse.bookmark
                        setBookmarkState(state)
                    }
                    Status.ERROR -> {
                        _containBinding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return true
    }

    private fun setBookmarkState(state: Boolean) {
        if (menu == null) return
        val menuItem = menu?.findItem(R.id.action_bookmark)
        menuItem?.icon = if (state) ContextCompat.getDrawable(this, R.drawable.ic_bookmarked)
        else ContextCompat.getDrawable(this, R.drawable.ic_bookmark)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_bookmark) {
            viewModel.setBookmark()
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_COURSE = "extra_course"
    }
}