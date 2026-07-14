import os
import json
import sys

def create_item_models(item_names):
    """
    Создаёт JSON файлы моделей для указанных предметов
    """
    # Базовый путь для моделей
    base_dir = "."
    
    # Создаём директорию, если её нет
    os.makedirs(base_dir, exist_ok=True)
    
    # Шаблон JSON (базовый)
    base_template = {
        "parent": "item/generated",
        "textures": {
            "layer0": "hbm:item/{name}"
        }
    }
    
    # Разделяем имена по запятой и удаляем лишние пробелы
    names = [name.strip() for name in item_names.split(',')]
    created_count = 0
    
    for name in names:
        if not name:  # Пропускаем пустые имена
            continue
        
        # Создаём КОПИЮ шаблона для каждого предмета
        template = base_template.copy()
        template["textures"] = base_template["textures"].copy()
        
        # Путь к файлу
        file_path = os.path.join(base_dir, f"{name}.json")
        
        # Обновляем текстуру с именем предмета
        template["textures"]["layer0"] = f"hbm:item/{name}"
        
        # Записываем JSON в файл
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(template, f, indent=2, ensure_ascii=False)
        
        print(f"✓ Создан файл: {file_path}")
        created_count += 1
    
    return created_count

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Использование: python script.py имя1,имя2,имя3")
        print("Пример: python script.py syringe_metal_empty,syringe_metal_medx,radaway_strong")
        sys.exit(1)
    
    item_names = sys.argv[1]
    count = create_item_models(item_names)
    print(f"\n✅ Готово! Создано файлов: {count}")