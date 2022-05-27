abstract class UpdateHandler {
  Future<void> handle({required String downloadUrl, void Function(int, int)? onReceiveProgress});
}
