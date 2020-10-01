package com.e.jetpackexample.di.main

import com.e.jetpackexample.ui.main.account.AccountFragment
import com.e.jetpackexample.ui.main.account.ChangePasswordFragment
import com.e.jetpackexample.ui.main.account.UpdateAccountFragment
import com.e.jetpackexample.ui.main.blog.BlogFragment
import com.e.jetpackexample.ui.main.blog.UpdateBlogFragment
import com.e.jetpackexample.ui.main.blog.ViewBlogFragment
import com.e.jetpackexample.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}