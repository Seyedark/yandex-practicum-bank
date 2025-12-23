import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create account"
    request {
        method POST()
        url "/account/balance"
        headers {
            contentType(applicationJson())
        }
        body([
                login: "login",
                currency: "RUB"
        ])
    }
    response {
        status OK()
    }
}