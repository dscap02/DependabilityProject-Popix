document.addEventListener('DOMContentLoaded', function () {
    const updateButtons = document.querySelectorAll('.update-qty');

    updateButtons.forEach(button => {
        button.addEventListener('click', function () {
            const productId = this.getAttribute('data-id');

            // Trova il relativo input number
            const quantityInput = this.previousElementSibling; // Trova l'elemento input adiacente
            const newQty = Number.parseInt(quantityInput.value, 10);

            if (newQty > 0) {
                fetch(contextPath + '/UpdateCartServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `productId=${productId}&qty=${newQty}`
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.success) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Successo!',
                                text: 'Quantità aggiornata!',
                                showConfirmButton: false,
                                timer: 1500
                            }).then(() => location.reload());
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Errore!',
                                text: data.message,
                                showConfirmButton: true
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            } else {
                Swal.fire('Errore', 'La quantità deve essere almeno 1.', 'error');
                quantityInput.value = 1; // Reimposta la quantità a 1
            }
        });
    });
});
