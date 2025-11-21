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
                balanceFrom: 1,
                loginTo    : "login2",
                balanceTo  : 1
        ])
    }
    response {
        status OK()
    }
}