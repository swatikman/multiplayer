package ga.chrom_web.player.multiplayer.data

class Response<E> {
    enum class Status {
        OK, ERROR
    }

    var status: Status
    var data: E?
        private set

    constructor(data: E, status: Status) {
        this.status = status
        this.data = data;
    }

    constructor(status: Status) {
        this.status = status
        this.data = null
    }

    companion object {
        fun <E> successResponse(data: E): Response<E> {
            return Response(data, Status.OK)
        }

        fun <E> errorResponse(): Response<E> {
            return Response(Status.ERROR)
        }

    }
}