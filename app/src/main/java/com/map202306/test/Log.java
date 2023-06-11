package com.map202306.test;


//로그 저장기능 구현

public class Log {
 /*    File? file;

    @Override
    void init() async {
        super.init();
        final path = await generateLogFilePath();
        file = File(path);
    }

    @Override
    void output(OutputEvent event) {
        event.lines.forEach(logWriteFile);
    }

    Future<String> generateLogFilePath() async {
        final now = DateTime.now();
        final formatter = DateFormat('yyyy-MM-dd');
        final fileName = '${formatter.format(now)}.log';
        final appDocDir = await getApplicationDocumentsDirectory();
        final logDir = await Directory('${appDocDir.path}/logs').create();
        return '${logDir.path}/$fileName';
    }

    void logWriteFile(String msg) {
        if (file != null) {
            file!.writeAsStringSync('$msg\n', mode: FileMode.writeOnlyAppend, flush: true);
        }
    }

    Future<String?> _getLogPath() async {
        final appDocDir = await getApplicationDocumentsDirectory();
        final logDir = Directory('${appDocDir.path}/logs');
        final isExists = await logDir.exists();

        if (!isExists) {
            logger.e('로그 폴더를 찾을 수 없음.');
            return null;
        }

        try {
            final deviceInfoPlugin = DeviceInfoPlugin();
            var deviceInfo = '';

            switch (defaultTargetPlatform) {
                case TargetPlatform.android:
                    final androidInfo = await deviceInfoPlugin.androidInfo;
                    deviceInfo = '${androidInfo.model}_Android ${androidInfo.version.release}';
                case TargetPlatform.iOS:
                    final iosInfo = await deviceInfoPlugin.iosInfo;
                    deviceInfo = '${iosInfo.model}_iOS ${iosInfo.systemVersion}';
                default:
                    deviceInfo = 'UnknownDeviceInfo';
            }

            final zipFileName =
            '${deviceInfo}_${DateFormat('yyyy-MM-dd HH:mm:ss').format(DateTime.now())}_logs.zip';
            final zipFilePath = '${appDocDir.path}/$zipFileName';
            _startCompressLogFile(zipFilePath, logDir);
            return zipFilePath;
        } catch (e, stack) {
            logger.e(e.toString(), e, stack);
            return null;
        }
    }*/
}
