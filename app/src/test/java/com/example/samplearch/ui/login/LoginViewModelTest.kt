package com.example.samplearch.ui.login

import android.security.keystore.UserNotAuthenticatedException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.samplearch.data.LoginRepository
import com.example.samplearch.data.Result
import com.example.samplearch.data.model.LoggedInUser
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @MockK
    private lateinit var repository: LoginRepository

    private lateinit var mockedUiStateObserver: Observer<LoginUiState>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTaskExecutorRule = MainCoroutineRule()

    private val fakeSuccessLogin = "gfreitas"
    private val fakeSuccessPass = "1234567"

    private val fakeErrorLogin = "teste@"
    private val fakeErrorPass = "123"

    private val fakeDisplayName = "Joe Due"
    private val fakeUser = LoggedInUser("123", fakeDisplayName)


    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = LoginViewModel(repository)

        mockedUiStateObserver = spyk(Observer {})
        viewModel.uiState.observeForever(mockedUiStateObserver)
    }

    @Test
    fun `should return success when login request receive right credentials`() = runBlockingTest{
        val stateSlot = mutableListOf<LoginUiState>()

        coEvery { repository.login(any(), any()) } returns Result.Success(fakeUser)

        viewModel.login(fakeSuccessLogin, fakeSuccessPass)

        verify(exactly = 2) {mockedUiStateObserver.onChanged(capture(stateSlot))}

        verifyOrder {
            mockedUiStateObserver.run {
                onChanged(LoginUiState.Loading)
                onChanged(any<LoginUiState.Success>())
            }
        }
        assertThat(
            (stateSlot.last() as LoginUiState.Success).loggedInUser.displayName,
            equalTo(fakeDisplayName)
        )
    }

    @Test
    fun `should return error when login request receive wrong credentials`() =
        runBlockingTest {
            val stateSlot = mutableListOf<LoginUiState>()

            coEvery { repository.login(any(), any()) } returns Result.Error(UserNotAuthenticatedException())

            viewModel.login(fakeErrorLogin, fakeErrorPass)

            verify(exactly = 2) { mockedUiStateObserver.onChanged(capture(stateSlot)) }

            verifyOrder {
                mockedUiStateObserver.run{
                    onChanged(LoginUiState.Loading)
                    onChanged(any<LoginUiState.Error>())
                }
            }

            assertThat(
                (stateSlot.last() as LoginUiState.Error).error,
                instanceOf(UserNotAuthenticatedException::class.java)
            )
        }

    @After
    fun tearDown() {
        unmockkAll()
        viewModel.uiState.removeObserver(mockedUiStateObserver)
    }

}