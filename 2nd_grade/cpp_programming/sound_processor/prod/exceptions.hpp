#ifndef exceptions_h
#define exceptions_h


#include <string>


class SoundProcessorError : public std::runtime_error {
public:
    explicit SoundProcessorError(const std::string& message) : std::runtime_error(message) {}
};


class ConfigParseError : public SoundProcessorError {
public:
    explicit ConfigParseError(const std::string& message) : SoundProcessorError("Configuration Parse Error: " + message) {}
};


class ArgumentsError : public ConfigParseError {
public:
    explicit ArgumentsError(const std::string& message) : ConfigParseError("Arguments Error: " + message) {}
};


class InvalidCommandError : public ConfigParseError {
public:
    explicit InvalidCommandError(const std::string& message) : ConfigParseError("Invalid Command Error: " + message) {}
};


class UnknownCommandError : public ConfigParseError {
public:
    explicit UnknownCommandError(const std::string& message) : ConfigParseError("Unknown Command Error: " + message) {}
};


class FileReadError : public SoundProcessorError {
public:
    explicit FileReadError(const std::string& message) : SoundProcessorError("File Read Error: " + message) {}
};


class UnsupportedFormatError : public SoundProcessorError {
public:
    explicit UnsupportedFormatError(const std::string& message) : SoundProcessorError("Unsupported Format Error: " + message) {}
};


#endif /* exceptions_h */
