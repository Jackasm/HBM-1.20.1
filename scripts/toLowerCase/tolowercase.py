import os

def rename_files_to_lowercase(overwrite=True):
    """Переименовывает все файлы в текущей папке в нижний регистр
    
    Args:
        overwrite: Если True, удаляет существующие файлы с нижним регистром
    """
    
    current_dir = os.getcwd()
    files = os.listdir(current_dir)
    
    renamed_count = 0
    overwritten_count = 0
    
    for filename in files:
        full_path = os.path.join(current_dir, filename)
        
        # Пропускаем директории
        if os.path.isdir(full_path):
            continue
            
        new_filename = filename.lower()
        
        # Если имя уже в нижнем регистре, пропускаем
        if filename == new_filename:
            continue
            
        new_full_path = os.path.join(current_dir, new_filename)        
        
        # Переименовываем файл
        os.rename(full_path, new_full_path)
        renamed_count += 1
        print(f"Переименован: {filename} -> {new_filename}")
    
    print(f"\nГотово! Переименовано {renamed_count} файлов, удалено {overwritten_count} дубликатов.")

if __name__ == "__main__":
    rename_files_to_lowercase(overwrite=True)