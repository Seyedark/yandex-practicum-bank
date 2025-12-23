import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should transfer money between accounts"
    request {
        method PATCH()
        url "/account/transfer"
        headers {
            contentType(applicationJson())
        }
        body([
                loginFrom  : "login1",
                currencyFrom: "RUB",
                balanceFrom: 1,
                loginTo    : "login2",
                currencyTo: "RUB",
                balanceTo  : 1
        ])
    }
    response {
        status OK()
    }
}