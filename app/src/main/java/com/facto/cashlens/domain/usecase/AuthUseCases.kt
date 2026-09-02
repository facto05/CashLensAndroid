package com.facto.cashlens.domain.usecase

import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<Unit> =
        repo.login(email, password)
}

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<Unit> =
        repo.register(email, password)
}

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): Resource<Unit> = repo.logout()
}

class IsLoggedInUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): Boolean = repo.isLoggedIn()
}
