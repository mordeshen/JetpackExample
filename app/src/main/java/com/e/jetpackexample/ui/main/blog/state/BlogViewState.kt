package com.e.jetpackexample.ui.main.blog.state

import com.e.jetpackexample.models.BlogPost

data class BlogViewState(
    //BlogFragment vars
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFields vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields()
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false

    )

    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    )
}